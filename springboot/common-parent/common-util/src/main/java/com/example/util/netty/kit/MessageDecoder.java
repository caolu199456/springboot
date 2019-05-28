package com.example.util.netty.kit;

import com.example.util.common.KryoKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes()<4) {

            //说明消息有问题
            return;
        }
        /**
         * 读取消息总共长度
         */
        int messageLength = byteBuf.readInt();


        if (messageLength > byteBuf.readableBytes()) {
            //消息有问题
            return;
        }

        byte[] bytes = new byte[messageLength];

        byteBuf.readBytes(bytes);

        list.add(KryoKit.toObject(bytes));

    }
}
