package com.example.util.netty.kit;

import com.example.util.netty.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyMessageToByteEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        Message message = (Message) o;

        ByteBuf buffer = Unpooled.buffer();

        buffer.writeInt(message.getMessageId());
        buffer.writeInt(message.getBodyLength());
        buffer.writeBytes(message.getBody().getBytes());
        channelHandlerContext.writeAndFlush(buffer);
    }
}
