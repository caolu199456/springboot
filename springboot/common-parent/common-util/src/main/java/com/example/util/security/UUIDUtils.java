package com.example.util.security;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * id的帮助类
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-03-04 9:39:00
 */
public class UUIDUtils {
    /**
     * 生成32为的uuid
     * @return
     */
    public static String gen32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成base64压缩后的uuid  128为用64进制表示 最长位22位
     *
     * 这里的64个字符采用A-Z a-z 0-9 另外- _ 两个特殊字符
     * @return
     */
    public static String genCompressedUUID() {
        //128长度的二进制
        UUID uuid = UUID.randomUUID();

        //高64位
        long msb = uuid.getMostSignificantBits();
        //低64位
        long lsb = uuid.getLeastSignificantBits();

        //我们用16位的byte数组能表示 16*8=128位的二进制
        byte[] data = new byte[16];
        for (int i = 0; i < 8; i++) {
            data[i] = (byte) ((msb >> ((7 - i) * 8))&0xFF);
            data[8+i] = (byte) ((lsb >> ((7 - i) * 8))&0xFF);
        }
        byte[] encodeData = Base64.getUrlEncoder().encode(data);
        //总共24位我们不需要最后两位==  等号的ASCII码为61
        return new String(encodeData,0,22);
    }
    /**
     * 解压压缩后的id
     *
     * @return
     */
    public static UUID uncompressUUID(String compressedUUID) {
        byte[] src = Base64.getUrlDecoder().decode((compressedUUID+"==").getBytes());
        return new UUID(ByteBuffer.wrap(src, 0, 8).getLong(), ByteBuffer.wrap(src, 8, 8).getLong());
    }
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            String s = genCompressedUUID();
            uncompressUUID(s);
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);

    }

}
