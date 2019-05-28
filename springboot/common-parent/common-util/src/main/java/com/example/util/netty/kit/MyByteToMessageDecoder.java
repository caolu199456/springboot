package com.example.util.netty.kit;

import com.example.util.netty.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyByteToMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Message message = new Message();
        message.setMessageId(byteBuf.readInt());
        message.setBodyLength(byteBuf.readInt());

        if (byteBuf.readableBytes() < message.getBodyLength()) {
            //如果数据长度小于设定的数据，则处于缓存状态
            byteBuf.resetReaderIndex();
            //缓存当前数据，等待数据接入
            return;
        }
        ByteBuf body = Unpooled.buffer(message.getBodyLength());
        byteBuf.readBytes(body);

        byteBuf.markReaderIndex();//消息堆积防止进入死循环
        System.out.println("readIndex:"+byteBuf.readerIndex());
        System.out.println("writeIndex:"+byteBuf.writerIndex());
        System.out.println("readableBytes:"+byteBuf.readableBytes());

        message.setBody(new String(body.array()));
        list.add(message);


    }

}
