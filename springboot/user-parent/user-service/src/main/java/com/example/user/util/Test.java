package com.example.user.util;

import java.sql.SQLException;

public class Test {
    /**
     * 将受到的字符进行转义
     *
     * @param recData
     * @return
     */
    public static String parseMessage(String recData) {
        String replace = recData.replace("7d01", "7d").replace("7d02", "7e");
        return replace.substring(2, replace.length() - 2);
    }
    /**
     * 将受到的字符进行转义
     *
     * @param postData
     * @return
     */
    public static String buildMessage(String postData) {
        String replace = "7e"+postData.replace("7d", "7d01").replace("7e", "7d02")+"7e";
        return replace;
    }
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            System.out.println(i&255);
        }
    }

}
