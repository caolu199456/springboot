package com.example.util.common;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;

/**
 * 数字之间的操作
 *
 * @author: CL
 * @email: caolu @sunseaaiot.com
 * @date: 2019 -03-04 9:10:00
 */
public class NumberUtils {
    /**
     * int转长度为4的byte数组
     *
     * @param value the value
     * @return byte [ ]
     */
    public static byte[] intToBytes(int value) {
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt(value);
        return allocate.array();
    }

    /**
     * 4位byte转int
     *
     * @param bytes the bytes
     * @return int
     */
    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes, 0, 4).getInt();
    }

    /**
     * long转长度为8的byte数组
     *
     * @param value the value
     * @return byte [ ]
     */
    public static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        return buffer.array();
    }

    /**
     * 8位byte转long
     *
     * @param bytes the bytes
     * @return long
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 8);
        return buffer.getLong();
    }

    /**
     * double转长度为8的byte数组
     *
     * @param value the value
     * @return byte [ ]
     */
    public static byte[] doubleToBytes(double value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(value);
        return buffer.array();
    }

    /**
     * 8位byte转double
     *
     * @param bytes the bytes
     * @return double
     */
    public static double bytesToDouble(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 8);
        return buffer.getDouble();
    }

    /**
     * float转长度为4的byte数组
     *
     * @param value the value
     * @return byte [ ]
     */
    public static byte[] floatToBytes(float value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        return buffer.array();
    }

    /**
     * 8位byte转float
     *
     * @param bytes the bytes
     * @return float
     */
    public static float bytesToFloat(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 4);
        return buffer.getFloat();
    }

    /**
     * 数字转为二进制 例如数字1需要转为长度为8的二进制则会出现00000001
     *
     * @param number the number
     * @param len    转为多长的二进制 如果高位不足则补0
     * @return string
     */
    public static String numberToFullBinaryStr(long number, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append((number >> (len - 1 - i)) & 1);
        }
        return sb.toString();
    }

    /**
     * 二进制转为数字
     *
     * @param binaryStr 二进制 传入 010 或者 10都代表数字2
     * @return long
     */
    public static long binaryStrToNumber(String binaryStr) {
        return Long.parseLong(binaryStr, 2);
    }

    /**
     * 数字转16进制
     *
     * @param number 数字
     * @return string
     */
    public static String numberToHexStr(long number) {
        return Long.toHexString(number);
    }

    /**
     * 16进制转数字
     *
     * @param hexStr 16进制数字 如 FF
     * @return long
     */
    public static long numberToHexStr(String hexStr) {
        return Long.parseLong(hexStr, 16);
    }

    /**
     * byte数组编码为16进制
     * @param bytes the bytes
     * @return the string
     */
    public static String encodeBytes(byte[] bytes){
        return Hex.encodeHexString(bytes);
    }

    /**
     * 16进制解码为byte数组
     *
     * @param hexStr the hex str
     * @return the bytes
     */
    public static byte[] decodeHexStr(String hexStr){
        try {
            return Hex.decodeHex(hexStr);
        } catch (DecoderException e) {
            throw new RuntimeException("16进制解码出错");
        }
    }
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        System.out.println(numberToHexStr(Integer.MAX_VALUE));
    }
}
