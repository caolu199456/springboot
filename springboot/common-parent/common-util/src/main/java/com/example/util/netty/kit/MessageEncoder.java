package com.example.util.netty.kit;

import com.example.util.common.KryoKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        byte[] bytes = KryoKit.fromObject(o);

        byteBuf.writeInt(bytes.length);

        byteBuf.writeBytes(bytes);

    }
}
