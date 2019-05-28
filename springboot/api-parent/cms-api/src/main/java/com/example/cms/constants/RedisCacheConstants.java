package com.example.cms.constants;

/**
 * 在这里编写说明
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-04-04 11:22:00
 */
public class RedisCacheConstants {
    private static final String PROJECT_NAME = "BASE_API_";
    public static final String TOKEN_PREFIX = PROJECT_NAME + "SYS_USER_TOKEN:";
    /**
     * 登录的时候返回给前端  key为md5  value私钥 公钥返回给前端
     */
    public static String LOGIN_ID = PROJECT_NAME + "LOGIN_ID:";

    public static String CAPTCHA_ID = PROJECT_NAME + "CAPTCHA_ID";
}
