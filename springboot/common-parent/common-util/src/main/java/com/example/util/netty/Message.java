package com.example.util.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Message {
    private int messageId;
    private int bodyLength;
    private String body;
    public byte[] getBytes() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(messageId);
        byteBuf.writeInt(bodyLength);
        byteBuf.writeBytes(body.getBytes());
        return byteBuf.array();
    }
}
