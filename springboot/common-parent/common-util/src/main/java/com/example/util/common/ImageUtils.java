package com.example.util.common;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtils {
    public static String base64Encode(ByteArrayOutputStream out){
        try (ByteArrayOutputStream baos = out){
            return "data:image/jpg;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("编码图片出错");
        }
    }
}
