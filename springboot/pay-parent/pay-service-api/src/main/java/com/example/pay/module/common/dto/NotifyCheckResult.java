package com.example.pay.module.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 校验返回值发现success为true后处理其他相关逻辑 到最后再更新订单为已支付 然后在告诉服务器这边的处理结果
 * 以支付宝买柜子里边的水场景为例
 * try{
 *      if (success){
 *        //插入用户买水记录相关信息
 *
 *        ....
 *
 *        //然后更新订单
 *        out.println("success")
 *      }
 *
 * }catch(Exception e){
 *     out.println("fail")
 * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyCheckResult implements Serializable {
    /**
     * 校验是否成功 并不代表退款成功
     * 对于支付宝要告诉服务器 success字符串否则一直重试
     * 对于微信要告诉服务器    String xml = "<xml>\n" +
     * "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
     * "  <return_msg><![CDATA[OK]]></return_msg>\n" +
     * "</xml>";
     */
    private boolean checkSuccess = false;


    /**
     * 商户订单号 一定返回
     */
    private String outTradeNo;
    /**
     * 交易号 一定返回
     */
    private String tradeNo;


    /**
     * 1支付成功2退款成功
     */
    private Byte tradeStatusType;

    /**
     * 支付是否成功
     */
    private boolean paySuccess = false;

    /**
     * 交易成功实践
     */
    private Date paySuccessTime;


    /**
     * 退款是否成功
     */
    private boolean refundSuccess = false;

    /**
     * 商户退款单号退款成功后会携带（tradeStatusType==2)
     */
    private String outRefundNo;
    /**
     * 退款成功时间
     */
    private Date refundSuccessTime;

    /**
     * 失败的原因
     */
    private String failReason;
}
