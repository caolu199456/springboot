package com.example.pay.constants;

public class RedisContants {
    private static final String PROJECT_NAME = "PAY_SERVICE_";
    /**
     * 当退款的时候锁定订单一分钟 key+订单号 防止并发发起两次产生两个订单号造成多余退款
     */
    public static final String ORDER_REFUND_KEY = PROJECT_NAME+"ORDER_REFUND_KEY:";
}
