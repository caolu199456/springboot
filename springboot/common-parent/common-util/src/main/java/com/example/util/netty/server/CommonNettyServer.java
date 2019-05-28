package com.example.util.netty.server;

import com.example.util.netty.Message;
import com.example.util.netty.kit.MyByteToMessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

public class CommonNettyServer {

    private static Logger logger = Logger.getLogger(CommonNettyServer.class);

    private int port;

    public CommonNettyServer(int port) {
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
                            ch.pipeline().addLast(new MyByteToMessageDecoder());//如果取消了分割符解码，就会出现TCP粘包之类的问题了
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    logger.info(ctx.name() + "连接了");
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


                                    System.out.println(msg);
                                    System.out.println(((Message)msg).getBodyLength());
                                    ReferenceCountUtil.release(msg); // (2)

                                }
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
                                    // Close the connection when an exception is raised.
                                    cause.printStackTrace();
                                    ctx.close();
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
        new CommonNettyServer(port).run();
    }
}
