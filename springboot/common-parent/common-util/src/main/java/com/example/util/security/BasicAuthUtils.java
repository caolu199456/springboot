package com.example.util.security;

import com.alibaba.fastjson.JSON;
import com.example.util.http.HttpRestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * basic授权认证
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-03-18 16:26:00
 */
public class BasicAuthUtils {

    /**
     * 返回basic auth请求头
     * @param username
     * @param password
     * @return
     */
    public static Map<String, String> getBasicAuthHeader(String username, String password) {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(Charset.forName("UTF-8"))));
        return header;
    }

    /**
     * 校验basic授权是否正确
     * @param auth
     * @param username
     * @param password
     * @return
     */
    public static boolean checkBasicAuth(String auth, String username, String password) {
        if (StringUtils.isBlank(auth) || !auth.startsWith("Basic ")) {
            return false;
        }
        try {
            byte[] decode = Base64.getDecoder().decode(auth.substring(6));

            String usernameAndPassword = new String(decode, Charset.forName("UTF-8"));
            String[] split = usernameAndPassword.split(":");
            return username.equals(split[0]) && password.equals(split[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        HttpRestUtils defaultInstance = HttpRestUtils.getDefaultInstance();
        String body = (String) defaultInstance.get("http://192.168.2.100/test/", null, getBasicAuthHeader("caolu", "root"), String.class).getRight();

        System.out.println(body);
    }
}
