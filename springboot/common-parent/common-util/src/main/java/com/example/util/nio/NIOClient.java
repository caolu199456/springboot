package com.example.util.nio;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class NIOClient {
    private static SocketChannel sc;
    private static Selector selector;
    private static Cache<SocketChannel, SelectionKey> cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public NIOClient() throws IOException {

    }

    public void init() throws IOException {
        sc = SocketChannel.open();
        sc.configureBlocking(false);
        selector = Selector.open();
        sc.connect(new InetSocketAddress("127.0.0.1", 8000));
        sc.register(selector, SelectionKey.OP_CONNECT);
        connectAndSendMsg();

    }

    private void connectAndSendMsg() throws IOException {

        while (true) {
            int select = selector.select();
            if (select <= 0) {
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                handKey(next);
                iterator.remove();
            }
        }

    }

    private void handKey(SelectionKey key) {
        try {
            if (key.isValid()) {
                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    //如果正在连接，则完成连接
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }
                    cache.put(channel, key);
                    //连接成功后，注册接收服务器消息的事件
                    System.out.println("客户端连接成功");

                    sc.configureBlocking(false);
                    cache.getIfPresent(sc).interestOps(SelectionKey.OP_WRITE);

                } else if (key.isReadable()) { //可写

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

                }
            }
        } catch (Exception e) {

        }

    }

    public static void main(String[] args) throws IOException {
        new Thread(){
            @Override
            public void run() {
                try {
                    new NIOClient().init();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                ByteBuffer wrap = ByteBuffer.wrap(("现在时刻:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).getBytes());
                try {
                    sc.write(wrap);

                    sc.configureBlocking(false);
                    cache.getIfPresent(sc).interestOps(SelectionKey.OP_READ);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, 10000, 5000);
    }

}
