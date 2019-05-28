package com.example.pay.module.order.service;

import com.example.pay.module.order.dto.request.OrderRequestParams;
import com.example.pay.module.order.dto.response.OrderCreatedResponseDto;

/**
 * app相关支付
 */
public interface OrderAppPayService {

    /**
     * 创建一个支付宝订单 并且存入数据库
     * @param orderRequestParams
     * @return
     */
    OrderCreatedResponseDto createZfbOrder(OrderRequestParams orderRequestParams);

    /**
     * 创建一个微信订单 并且存入数据库
     * @param orderRequestParams
     * @return
     */
    OrderCreatedResponseDto createWxOrder(OrderRequestParams orderRequestParams);
}
