package com.example.pay.module.common.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.pay.module.common.dto.NotifyCheckResult;
import com.example.pay.module.common.service.NotifyCheckService;
import com.example.pay.sdk.wx.WXPayConfig;
import com.example.pay.sdk.wx.WXPayUtil;
import com.example.util.spring.SpringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 这里的回调包含支付宝异步回调（全额退款不发生回调 同步退款然后标记退款成功即可） 微信H5/App退款需要依赖回调
 */
@Service
public class NotifyCheckServiceImpl implements NotifyCheckService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(NotifyCheckService.class);
    @Resource(name = "wxH5Pay")
    private WXPayConfig wxH5PayConfig;
    @Resource(name = "wxAppPay")
    private WXPayConfig wxAppPayConfig;
    @Override
    public NotifyCheckResult checkZfbAllNotify(Map<String, String[]> notifyParams) {
        Map<String, String> params = new HashMap<>();
        //获取支付宝POST过来反馈信息
        for (Map.Entry<String, String[]> entry : notifyParams.entrySet()) {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), ','));
        }
        LOGGER.info("支付宝验签参数为{}", JSON.toJSONString(params));

        NotifyCheckResult notifyCheckResult = new NotifyCheckResult();
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, SpringUtils.getConfigValue("pay.zfb.publicKey"), "UTF-8", "RSA2");
            if (flag) {
                notifyCheckResult.setCheckSuccess(true);
                notifyCheckResult.setOutTradeNo(params.get("out_trade_no"));
                notifyCheckResult.setTradeNo(params.get("trade_no"));

                if (StringUtils.isNotBlank(params.get("refund_fee"))) {
                    //这个if是没有用的不要依赖。。。。。。。。。。。。。。。。。

                    //退款类型 支付宝发起后就知道是否可以退款成功 不需要回调这里是多余的
                    //这里有个坑  10元分10次退每次退1元 前9笔有回调后期 最后一笔是交易关闭不触发异步通知(即全额退款 所以我们要用同步接口去判断退款成功不依赖异步)
                    /**
                     * 在这个退款接口是没有传入异步地址的位置的，退款的异步通知是依据支付接口的触发条件来触发的，异步通知也是发送到支付接口传入的异步地址上。
                     *
                     *   （1）部分退款：部分退款交易状态是处于TRADE_SUCCESS（交易成功），此时因部分退款导致交易金额变动，都会触发异步通知
                     *   （2）全额退款：交易成功后全额退款，交易状态会转为TRADE_CLOSED（交易关闭），此时根据不同的支付接口触发条件也不同，
                     *   例如APP支付接口TRADE_CLOSED（交易关闭）状态触发异步，此时就会收到全额退款的异步通知。而电脑网站支付TRADE_CLOSED（交易关闭）状态不会触发异步，就不会有全额退款的异步通知
                     */
                    notifyCheckResult.setTradeStatusType(Byte.valueOf("2"));
                    if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
                        //如果有这个代表退款
                        notifyCheckResult.setRefundSuccess(true);
                        notifyCheckResult.setOutRefundNo(params.get("out_biz_no"));
                        notifyCheckResult.setRefundSuccessTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(params.get("gmt_refund")));
                    }else {
                        //退款失败原因
                        notifyCheckResult.setFailReason(params.get("trade_status")+":退款失败");
                    }
                }else {
                    //支付类型
                    notifyCheckResult.setTradeStatusType(Byte.valueOf("1"));
                    if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
                        notifyCheckResult.setPaySuccess(true);
                        notifyCheckResult.setPaySuccessTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.get("gmt_payment")));
                    }else {
                        //支付失败原因
                        notifyCheckResult.setFailReason(params.get("trade_status")+":支付失败");
                    }
                }
            }
            return notifyCheckResult;
        } catch (Exception e) {
            LOGGER.error("支付宝验签失败");
        }
        return notifyCheckResult;
    }

    @Override
    public NotifyCheckResult checkWxH5PayNotify(String recStr) {
        LOGGER.info("微信H5支付成功回调验签参数为{}", recStr);
        NotifyCheckResult notifyCheckResult = new NotifyCheckResult();
        try {
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(recStr);
            boolean flag = WXPayUtil.isSignatureValid(xmlToMap, wxH5PayConfig.getKey());
            if (flag) {
                notifyCheckResult.setCheckSuccess(true);
                notifyCheckResult.setOutTradeNo(xmlToMap.get("out_trade_no"));
                notifyCheckResult.setTradeNo(xmlToMap.get("transaction_id"));

                notifyCheckResult.setTradeStatusType(Byte.valueOf("1"));
                if ("SUCCESS".equals(xmlToMap.get("return_code")) && "SUCCESS".equals(xmlToMap.get("result_code")) ) {
                    notifyCheckResult.setPaySuccess(true);
                    notifyCheckResult.setPaySuccessTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(xmlToMap.get("time_end")));
                }
                return notifyCheckResult;
            }
        } catch (Exception e) {
            LOGGER.error("微信H5支付成功回调校验出错",e);
        }
        return notifyCheckResult;
    }

    @Override
    public NotifyCheckResult checkWxH5RefundNotify(String recStr) {
        LOGGER.info("微信H5退款成功回调验签参数为{}", recStr);
        NotifyCheckResult notifyCheckResult = new NotifyCheckResult();
        try {
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(recStr);
            if ("SUCCESS".equals(xmlToMap.get("return_code"))) {

                //加密后的参数
                String encryptData= xmlToMap.get("req_info");
                String originalData = WXPayUtil.AES256ECBDecrypt(wxH5PayConfig.getKey(), encryptData);
                Map<String, String> data = WXPayUtil.xmlToMap(originalData);

                notifyCheckResult.setCheckSuccess(true);
                notifyCheckResult.setTradeStatusType(Byte.valueOf("2"));

                if ("SUCCESS".equals(data.get("refund_status"))) {
                    notifyCheckResult.setOutTradeNo(data.get("out_trade_no"));
                    notifyCheckResult.setTradeNo(data.get("transaction_id"));
                    notifyCheckResult.setRefundSuccess(true);
                    notifyCheckResult.setOutRefundNo(data.get("out_refund_no"));
                    notifyCheckResult.setRefundSuccessTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data.get("success_time") ));
                    return notifyCheckResult;
                }
            }

        } catch (Exception e) {
            LOGGER.error("微信H5退款成功回调校验出错",e);
        }
        return notifyCheckResult;
    }

    @Override
    public NotifyCheckResult checkWxAppPayNotify(String recStr) {
        LOGGER.info("微信APP支付成功回调验签参数为{}", recStr);
        NotifyCheckResult notifyCheckResult = new NotifyCheckResult();
        try {
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(recStr);
            boolean flag = WXPayUtil.isSignatureValid(xmlToMap, wxAppPayConfig.getKey());
            if (flag) {
                notifyCheckResult.setCheckSuccess(true);
                notifyCheckResult.setOutTradeNo(xmlToMap.get("out_trade_no"));
                notifyCheckResult.setTradeNo(xmlToMap.get("transaction_id"));

                notifyCheckResult.setTradeStatusType(Byte.valueOf("1"));
                if ("SUCCESS".equals(xmlToMap.get("return_code")) && "SUCCESS".equals(xmlToMap.get("result_code")) ) {
                    notifyCheckResult.setPaySuccess(true);
                    notifyCheckResult.setPaySuccessTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(xmlToMap.get("time_end")));
                }
                return notifyCheckResult;
            }
        } catch (Exception e) {
            LOGGER.error("微信H5支付成功回调校验出错",e);
        }
        return notifyCheckResult;
    }

    @Override
    public NotifyCheckResult checkWxAppRefundNotify(String recStr) {
        LOGGER.info("微信App退款成功回调验签参数为{}", recStr);
        NotifyCheckResult notifyCheckResult = new NotifyCheckResult();
        try {
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(recStr);
            if ("SUCCESS".equals(xmlToMap.get("return_code"))) {
                //加密后的参数
                String encryptData= xmlToMap.get("req_info");
                String originalData = WXPayUtil.AES256ECBDecrypt(wxAppPayConfig.getKey(), encryptData);
                Map<String, String> data = WXPayUtil.xmlToMap(originalData);

                notifyCheckResult.setCheckSuccess(true);
                notifyCheckResult.setTradeStatusType(Byte.valueOf("2"));

                if ("SUCCESS".equals(data.get("refund_status"))) {
                    notifyCheckResult.setOutTradeNo(data.get("out_trade_no"));
                    notifyCheckResult.setTradeNo(data.get("transaction_id"));
                    notifyCheckResult.setRefundSuccess(true);
                    notifyCheckResult.setOutRefundNo(data.get("out_refund_no"));
                    notifyCheckResult.setRefundSuccessTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data.get("success_time") ));

                }
                return notifyCheckResult;
            }

        } catch (Exception e) {
            LOGGER.error("微信App退款成功回调校验出错",e);
        }
        return notifyCheckResult;
    }

}
