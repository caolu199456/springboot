package com.example.pay.module.order.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 返回创建订单返回的参数
 */
@Data
public class OrderCreatedResponseDto implements Serializable {
    /**
     * 支付宝支付回返回String参数给前端
     */
    private Map<String,String> zfbMap;
    /**
     * 微信下单返回的map
     */
    private Map<String, String> wxMap;
}
