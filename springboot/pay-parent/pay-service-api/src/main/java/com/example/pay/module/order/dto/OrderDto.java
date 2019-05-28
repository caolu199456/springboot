package com.example.pay.module.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.lang.Long;
import java.util.Date;
import java.lang.String;
import java.lang.Byte;
import java.io.Serializable;

/**
 *
 *@author caolu
 *@date 2019-04-12 17:09:05
 */
@Data
public class OrderDto implements Serializable {

    /**
    * 
    */
    private Long id;
    /**
    * 1支付宝 2微信 3小程序
    */
    private Byte payType;
    /**
    * 1 h5 2 app
    */
    private Byte terminalType;
    /**
    * 产品名称
    */
    private String appId;
    /**
    * 如充电10分钟
    */
    private String productName;
    /**
    * 产品id(物联网设备代表设备id)可更换
    */
    private String productId;
    /**
    * 商户生成订单号
    */
    private String outTradeNo;
    /**
    * 交易号 第三方支付回掉返回
    */
    private String tradeNo;
    /**
    * 原价
    */
    private BigDecimal originalPrice;
    /**
    * 优惠价(即实际支付金额)
    */
    private BigDecimal specialPrice;
    /**
     * 总的退款金额
     */
    private BigDecimal totalRefundFee;
    /**
    * 支付时间
    */
    private Date payTime;

    /**
    * 购买人的id
    */
    private String buyerId;

    /**
     * 购买人的远程id
     */
    private String ipAddress;
    /**
    * 回调地址 支付宝支付回掉和退款回掉同一个
    */
    private String notifyUrl;


    /**
    * 状态0初始化1支付成功
    */
    private Byte status;
    /**
    * 最近一次更新时间
    */
    private Date updateTime;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 
    */
    private String remark;

}
