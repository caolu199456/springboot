package com.example.util.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/** 
* 功能：支付宝MD5签名处理核心文件，不需要修改
* 版本：3.3
* 修改日期：2012-08-17
* 说明：
* 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
* 该代码仅供学习和研究支付宝接口使用，只是提供一个
* */

public class MD5 {
	public static final String MD5="MD5";

    /**
     * @param args
     */
    public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };



    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String text, String key, String input_charset) {
    	text = text + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }
    
    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
    	text = text + key;
    	String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
    	if(mysign.equals(sign)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    public static String md5Encode(String origin, String charset) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (StringUtils.isBlank(charset)) {
                return DigestUtils.md5Hex(md.digest(origin.getBytes()));
            } else {
                return DigestUtils.md5Hex(md.digest(origin.getBytes(charset)));
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException 
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
    public static String getHash(File file, String hashType)
            throws Exception {
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        try (InputStream fis = new FileInputStream(file)){
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
        }

        return toHexString(md5.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }
    /**
     * 得到文件md5
     * @return 返回文件md5以及新的流  新的流需要自己关闭
     * @throws IOException
     * @param file
     */
    public static String getBigFileMD5(File file) throws Exception {
        return getHash(file, MD5);
    }



    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "error";
        }
    }
    public static void main(String[] args) throws IOException {
	}
}