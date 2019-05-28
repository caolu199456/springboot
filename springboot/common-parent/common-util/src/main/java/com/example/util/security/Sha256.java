package com.example.util.security;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {
    public static String getSha256(String str){
        if (str == null) {
            return null;
        }
        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            encdeStr = Hex.encodeHexString(hash);
            return encdeStr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getSha256("2222"));
    }


}
