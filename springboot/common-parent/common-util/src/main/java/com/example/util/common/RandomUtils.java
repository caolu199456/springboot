package com.example.util.common;

import java.security.SecureRandom;

/**
 * 获取随机数
 */
public class RandomUtils {

    public static final int[] NUMS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static final char[] CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    /**
     * 生成随机数字字符串
     * @param genLength 生成字符串的长度
     * @return
     */
    public static String genDigitalCode(int genLength) {
        int numLen = NUMS.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < genLength; i++) {
            sb.append(NUMS[new SecureRandom().nextInt(10)]);
        }
        return sb.toString();
    }
    /**
     * 生成随机字符字符串
     * @param genLength 生成字符串的长度
     * @return
     */
    public static String genCharsCode(int genLength) {
        int charLen = CHARS.length;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < genLength; i++) {
            sb.append(CHARS[new SecureRandom().nextInt(charLen)]);
        }
        return sb.toString();
    }
}
