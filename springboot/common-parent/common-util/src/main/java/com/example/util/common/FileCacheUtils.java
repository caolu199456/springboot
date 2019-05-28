package com.example.util.common;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Properties;

public class FileCacheUtils {

    /**
     * 缓存值到本地文件
     * @param fileName 要缓存的文件名
     * @param value 缓存的value
     */
    public static boolean writeValue(String fileName, String value) {
        try {
            File cachePath = new File(System.getProperty("user.home"),".my_cache");
            if (!cachePath.exists()) {
                cachePath.mkdir();
            }
            File cacheFile = new File(cachePath, fileName);
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            FileUtils.write(cacheFile,value,"utf-8");
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 缓存值到本地文件
     * @param fileName 要缓存的文件名
     */
    public static String getValue(String fileName) {
        File cachePath = new File(System.getProperty("user.home"), ".my_cache/" + fileName);
        if (!cachePath.exists()) {
            return null;
        }
        try {
            return FileUtils.readFileToString(cachePath, "utf-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
//        putValue("a", "ssssssssssss曹路你好");
        System.out.println(getValue("b"));

    }
}
