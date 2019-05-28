package com.example.mobile.constants;

/**
 * 在这里编写说明
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-04-04 11:22:00
 */
public class RedisCacheConstants {
    private static final String PROJECT_NAME = "MOBILE_API_";

    /**
     * 订单号锁定需要用到 PAY_SUCCESS_ORDER+订单号
     */
    public static String PAY_SUCCESS_ORDER = PROJECT_NAME + "PAY_SUCCESS_ORDER:";

    /**
     * 登录成功需要存入token
     */
    public static String H5_TOKEN = PROJECT_NAME + "H5_TOKEN:";
}
