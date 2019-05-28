package com.example.pay.module.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
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
 *@date 2019-04-15 15:02:52
 */
@Data
@TableName("pay_order_refund")
public class OrderRefundEntity {

    /**
     *
     */
    @TableId
    private Long id;
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 订单支付的金额
     */
    private BigDecimal totalAmount;

    /**
     * 退款订单号
     */
    private String outRefundNo;
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 1发起退款成功2退款成功3退款失败
     */
    private Byte refundStatus;
    /**
     * 退款成功时间
     */
    private Date refundSuccessTime;
    /**
     * 退款回调地址主要针对微信支付宝不需要
     */
    private String refundNotifyUrl;
    /**
     * 退款类型 1结算退款2人工退款
     */
    private Byte refundType;
    /**
     * 发起人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String editor;
    /**
     * 更新订单时间
     */
    private Date editTime;


}
