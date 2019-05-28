package com.example.pay.module.order.service;

import com.example.pay.module.common.dto.JsUserInfo;
import com.example.pay.module.order.dto.request.OrderRequestParams;
import com.example.pay.module.order.dto.response.OrderCreatedResponseDto;

/**
 * h5相关支付
 */
public interface OrderH5PayService {

    /**
     * 创建一个支付宝订单 并存入数据库
     * @param orderRequestParams
     * @return
     */
    OrderCreatedResponseDto createZfbOrder(OrderRequestParams orderRequestParams);


    /**
     * 创建一个微信订单 并存入数据库
     * @param orderRequestParams
     * @return
     */
    OrderCreatedResponseDto createWxOrder(OrderRequestParams orderRequestParams);
}
