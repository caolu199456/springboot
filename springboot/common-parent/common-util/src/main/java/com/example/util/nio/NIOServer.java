package com.example.util.nio;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NIOServer {
    private String ip;
    private int port;
    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;

    private Charset charset = Charset.defaultCharset();

    Cache<SocketChannel, String> cacheMsg = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public NIOServer(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);//阻塞
        serverSocketChannel.bind(new InetSocketAddress(ip, port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }


    private void run() throws IOException {
        while (true) {
            //无连接接入会阻塞
            int select = selector.select();
            if (select <= 0) {
                continue;

            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();//处理以后删除
                handKey(key);

            }
        }
    }

    private void handKey(SelectionKey key) {
        if (key.isValid()) {
            //可写
            SocketChannel sc = null;
            try {

                if (key.isAcceptable()) {
                    //这个事件是服务器注册的 所以channel来自服务器
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.register(selector,SelectionKey.OP_READ);

                } else if (key.isReadable()) {
                    //可读
                    sc = (SocketChannel) key.channel();
                    ByteBuffer recBuf = ByteBuffer.allocate(1024);

                    int len = sc.read(recBuf);

                    if (len > 0) {
                        String msg = new String(recBuf.array(), 0, len);
                        System.out.println("收到消息："+msg);

                        key.attach(msg);

                        sc.configureBlocking(false);

                        key.interestOps(SelectionKey.OP_WRITE);
                    }
//
                }else if (key.isWritable()) {
                    //写
                    sc = (SocketChannel) key.channel();
                    ByteBuffer recBuf = ByteBuffer.allocate(1024);

                    recBuf.put(("服务器收到你的消息" + key.attachment()).getBytes());
                    recBuf.flip();

                    sc.write(recBuf);

                    key.interestOps(SelectionKey.OP_READ);
//
                }
            } catch (Exception e) {
                key.cancel();
                try {
                    sc.socket().close();
                    sc.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOServer("127.0.0.1", 8000).run();
    }
}
