package com.example.util.netty.client;

import com.example.util.netty.Message;
import com.example.util.netty.kit.MyMessageToByteEncoder;
import com.example.util.netty.server.NettyServer;
import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

public class CommonNettyClient {
    private static Logger logger = Logger.getLogger(NettyServer.class);
    private static int port = 8080;
    static int i = 0;
    public static void main(String[] args) {

        EventLoopGroup workerGroup = null;
        Class socketChannel = null;
        if ("Linux".equalsIgnoreCase(System.getProperty("os.name"))) {
            workerGroup = new EpollEventLoopGroup();
            socketChannel = EpollSocketChannel.class;
        } else {
            workerGroup = new NioEventLoopGroup();
            socketChannel = NioSocketChannel.class;
        }
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(socketChannel); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MyMessageToByteEncoder());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {


                        }


                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                            System.out.println("客户端接收到回传消息:"+new Gson().toJson(msg));
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
            });

            // Start the client.
            ChannelFuture f = b.connect("127.0.0.1", port).sync(); // (5)

            // messageid | length | body
            for (int i = 0; i < 1000; i++) {
                String body = "it is a test"
                        ;
                Message message = new Message();
                message.setMessageId(i);
                message.setBodyLength(body.getBytes().length);
                message.setBody(body);

                f.channel().writeAndFlush(message);
//                Thread.sleep(10);
            }

            // Wait until the connection is closed.
//            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
