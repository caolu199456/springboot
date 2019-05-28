package com.example.pay.module.order.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 下单需要的参数
 */
@Data
public class OrderRequestParams implements Serializable {

    /**
     * 1支付宝 2微信 3小程序
     */
    private Byte payType;
    /**
     * 如充电10分钟
     */
    private String productName;
    /**
     * 产品id(物联网设备代表设备id)可更换
     */
    private String productId;
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    /**
     * 优惠价(即实际支付金额)
     */
    private BigDecimal specialPrice;

    /**
     * 购买人的id
     */
    private String buyerId;

    private String openId;


    /**
     * 购买人的远程id
     */
    private String ipAddress;

    /**
     * 回调地址 支付宝支付回掉和退款回掉同一个
     */
    private String notifyUrl;
    /**
     *
     */
    private String remark;
}
