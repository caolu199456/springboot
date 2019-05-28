package com.example.util.security;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {
    private static final String DEFAULT_SECRET = "AAADEEFFGRGTR";


    public static void main(String[] args) {
        String s = "曹路你好and";
        String encrypt = encrypt(s, null);
        System.out.println(encrypt);

        System.out.println(decrypt(encrypt,null));
    }

    /**
     * 根据密钥对指定的明文plainText进行加密.
     * @param plainText 明文
     * @param secret 密钥   为null采取默认密钥
     * @return 加密后的密文.
     */
    public static final String encrypt(String plainText,String secret) {
        Key secretKey = getKey(secret == null ? DEFAULT_SECRET : secret);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] p = plainText.getBytes("UTF-8");
            byte[] result = cipher.doFinal(p);
            String encoded = Base64.getEncoder().encodeToString(result);
            return encoded;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 根据密钥对指定的密文cipherText进行解密.
     *
     * @param cipherText 密文
     * @param secret 密钥   为null采取默认密钥
     * @return 解密后的明文.
     */
    public static final String decrypt(String cipherText,String secret) {
        Key secretKey = getKey(secret == null ? DEFAULT_SECRET : secret);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] c = Base64.getDecoder().decode(cipherText);
            byte[] result = cipher.doFinal(c);
            String plainText = new String(result, "UTF-8");
            return plainText;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Key getKey(String keySeed) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keySeed.getBytes());
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128,secureRandom);
            return generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
