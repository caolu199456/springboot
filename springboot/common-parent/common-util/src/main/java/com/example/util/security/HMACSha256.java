package com.example.util.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HMACSha256 {

    public static String genHMACSha256(String message, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(message.getBytes()));
            return hash;
        }catch (Exception e){
            System.out.println("Error");
        }
        return null;

    }


}
