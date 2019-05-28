package com.example.util.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

public class WebSocketServer {

    private static Logger logger = Logger.getLogger(WebSocketServer.class);

    private int port;

    public WebSocketServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = null; // (1)
        EventLoopGroup workerGroup = null;
        Class serverSocketChannel = null;

        if ("Linux".equalsIgnoreCase(System.getProperty("os.name"))) {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = new EpollEventLoopGroup();
            serverSocketChannel = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            serverSocketChannel = NioServerSocketChannel.class;
        }

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(serverSocketChannel) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-codec", new HttpServerCodec());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private ThreadLocal<WebSocketServerHandshaker> threadLocal=new ThreadLocal<>();//单例下需要用到
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    logger.info(ctx.name() + "连接了");
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {



                                    //http
                                    if (msg instanceof FullHttpRequest) {
                                        handleHttpRequest(ctx, (FullHttpRequest) msg);
                                    } else if (msg instanceof WebSocketFrame) {//websocket
                                        handleWebsocketFrame(ctx, (WebSocketFrame) msg);
                                    }

                                    ReferenceCountUtil.release(msg); // (2)

                                }
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
                                    // Close the connection when an exception is raised.
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                                private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg) {
                                    WebSocketServerHandshaker handshaker = threadLocal.get();
                                    if (handshaker == null) {
                                        return;
                                    }
                                    //关闭链路指令
                                    if (msg instanceof CloseWebSocketFrame) {
                                        handshaker.close(ctx.channel(), (CloseWebSocketFrame) msg.retain());
                                        return;
                                    }

                                    //PING 消息
                                    if (msg instanceof PingWebSocketFrame) {
                                        ctx.write(new PongWebSocketFrame(msg.content().retain()));
                                        return;
                                    }

                                    //非文本
                                    if (!(msg instanceof TextWebSocketFrame)) {
                                        throw new UnsupportedOperationException(String.format("%s frame type not support", msg.getClass().getName()));

                                    }

                                    logger.info("handleWebsocketFrame-->"+Thread.currentThread().getName());
                                    //应答消息
                                    String requset = ((TextWebSocketFrame) msg).text();


                                    ctx.channel().write(new TextWebSocketFrame(requset + " >>>>Now is " + System.currentTimeMillis()));
                                    ctx.channel().flush();

                                }

                                private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {

                                    //HTTP 请异常
                                    if (!msg.decoderResult().isSuccess() || !"websocket".equals(msg.headers().get("Upgrade"))) {
                                        System.out.println(msg.decoderResult().isSuccess());
                                        System.out.println(msg.headers().get("Upgrade"));
                                        sendHttpResponse(ctx, msg, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                                        return;
                                    }
                                    logger.info("handleHttpRequest-->"+Thread.currentThread().getName());
                                    //握手
                                    WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
                                    threadLocal.set(wsFactory.newHandshaker(msg));
                                    if (threadLocal.get() == null) {
                                        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());

                                    } else {
                                        threadLocal.get().handshake(ctx.channel(), msg);
                                    }
                                }

                                private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest msg, FullHttpResponse resp) {

                                    //响应
                                    if (resp.status().code() != 200) {
                                        ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), CharsetUtil.UTF_8);
                                        resp.content().writeBytes(buf);
                                        buf.release();
                                        setContentLength(resp, resp.content().readableBytes());
                                    }

                                    //非Keep-Alive,关闭链接
                                    ChannelFuture future = ctx.channel().writeAndFlush(resp);
                                    if (!isKeepAlive(resp) || resp.status().code() != 200) {
                                        future.addListener(ChannelFutureListener.CLOSE);
                                    }


                                }
                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new WebSocketServer(port).run();
    }
}
