package com.example.pay.sdk.wx;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.util.*;


public class WXPayUtil {

    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final Random RANDOM = new SecureRandom();

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilder documentBuilder = WXPayXmlUtil.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            WXPayUtil.getLogger().warn("Invalid XML, can not convert to map. Error message: {}. XML content: {}", ex.getMessage(), strXML);
            throw ex;
        }

    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        org.w3c.dom.Document document = WXPayXmlUtil.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }


    /**
     * 生成带有 sign 的 XML 格式字符串
     *
     * @param data Map类型数据
     * @param key API密钥
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key) throws Exception {
        return generateSignedXml(data, key, WXPayConstants.SignType.MD5);
    }

    /**
     * 生成带有 sign 的 XML 格式字符串
     *
     * @param data Map类型数据
     * @param key API密钥
     * @param signType 签名类型
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key, WXPayConstants.SignType signType) throws Exception {
        String sign = generateSignature(data, key, signType);
        data.put(WXPayConstants.FIELD_SIGN, sign);
        return mapToXml(data);
    }


    /**
     * 判断签名是否正确
     *
     * @param xmlStr XML格式数据
     * @param key API密钥
     * @return 签名是否正确
     * @throws Exception
     */
    public static boolean isSignatureValid(String xmlStr, String key) throws Exception {
        Map<String, String> data = xmlToMap(xmlStr);
        if (!data.containsKey(WXPayConstants.FIELD_SIGN) ) {
            return false;
        }
        String sign = data.get(WXPayConstants.FIELD_SIGN);
        return generateSignature(data, key).equals(sign);
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。使用MD5签名。
     *
     * @param data Map类型数据
     * @param key API密钥
     * @return 签名是否正确
     * @throws Exception
     */
    public static boolean isSignatureValid(Map<String, String> data, String key) throws Exception {
        return isSignatureValid(data, key, WXPayConstants.SignType.MD5);
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。
     *
     * @param data Map类型数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名是否正确
     * @throws Exception
     */
    public static boolean isSignatureValid(Map<String, String> data, String key, WXPayConstants.SignType signType) throws Exception {
        if (!data.containsKey(WXPayConstants.FIELD_SIGN) ) {
            return false;
        }
        String sign = data.get(WXPayConstants.FIELD_SIGN);
        return generateSignature(data, key, signType).equals(sign);
    }

    /**
     * 生成签名
     *
     * @param data 待签名数据
     * @param key API密钥
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key) throws Exception {
        return generateSignature(data, key, WXPayConstants.SignType.MD5);
    }

    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data 待签名数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key, WXPayConstants.SignType signType) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(WXPayConstants.FIELD_SIGN)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        if (WXPayConstants.SignType.MD5.equals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        }
        else if (WXPayConstants.SignType.HMACSHA256.equals(signType)) {
            return HMACSHA256(sb.toString(), key);
        }
        else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }


    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }


    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }


    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
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
     * 解密 微信AES解密
     * @param key 微信控制太所用到的key
     * @param encryptData 加密后的数据
     * @return
     */
    public static String AES256ECBDecrypt(String key,String encryptData) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //对key做Md5编码并转为小写
        SecretKey secretKey = new SecretKeySpec(MD5(key).toLowerCase().getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding","BC");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptData)));
    }
    /**
     * 日志
     * @return
     */
    public static Logger getLogger() {
        Logger logger = LoggerFactory.getLogger("wxpay java sdk");
        return logger;
    }

    /**
     * 获取当前时间戳，单位秒
     * @return
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis()/1000;
    }

    /**
     * 获取当前时间戳，单位毫秒
     * @return
     */
    public static long getCurrentTimestampMs() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        String a = "0KC5hoCVPyMaxOn5AfQpKjE1Z6Jm6PWI7uCnpCGZXy05YaqOuJXPeMwG6tMvcxwfJOe6u4vKyVZggcwmN0wI2y3CMYG6Z2uS4nluG1Aa/F5ctXurcNH9aGajo3D4Kx/J6hN7/p0IoInVl8gePBzjC5eRxC66ju2W+OaSOlDHT68yrkqeatfcYgjkYk6RGClMvKnl0mM6Nllwr9Bi6ca2vDNqA6TKIq52zu7o0/TBxRctpG4hnfssaQWhGQVMu50arWPyU+6f0aJ/GkJ21nJE/vD5NrjaKWxAOtRpHUWRkvOuUGsA2cx5snrZlRdtRNpAgs72RtoCGjp7IZx4mW88SdcIFG1DEgkzQe+IoVPWf3uAdS+ijbQ6xb7WFoHBrNzBJz84UZjN/tRIqA4qWv8ixIzQ4XUN7REBA4onBCeh/U7FckSuxmkIo8YcNDIrZoJs5KSuymQx6yxZ+Ik5TkGUP6KZZEhkICKy9nK6Tx/Nug2OgxKOhfCivIXe0hrbLd70IqWtypm4PDV8u6YB+uajtNrZY0r5166UZVFWYtn7eOj8OF2PqvWggsK82Mf+R2FykTH0p9NrnoR34h2eNrY4qtbi/J6MrJIGy54Ijuq9ywzV4CFSkJpqWyTezDOtPBJuD9qVNFVXavGYVpWEYGfxNwWYNJVlbNz9iXhWrKLdma4b6VYBJggQnkE53SLlXDuSt9o3BEPYkeWU1WTUhTLyZQosS527QbsigDaiM5Bn4C6Vp7un8Kz2A9j1VmAYV6SyxVivzTe83/2bU8WAORu8wCAfjCk9d9r0AeothIyiagZe9MAFBI9AiGEs5vnrmRgLRYHY7dg4DlpFzBKKucMGFLVLYu/olovrQ6cIfpqB2i7NArunn15Uu62MMhrAicZHUpp72OXT64u4VdOS15kiN8a8q7PjoP13r8IjaWQP9XR9ivAdThYDBbvJaGsCN8qduwa4ndwp7m/GG0c9V1SFo4nAlhI9RCAYkA+eoKIwtmROcgHZBXieOeNF/G3bIHmBAL5uPo2xZqyrvMpTice2lA==";

        try {
            System.out.println(AES256ECBDecrypt("4567qwer53321ty444rewq87656uyt", a));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
