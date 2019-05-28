package com.example.pay.module.common.service;

import com.example.pay.module.common.dto.NotifyCheckResult;

import java.util.Map;

/**
 * 异步回调参数校验校验
 */
public interface NotifyCheckService {
    /**
     * 校验支付宝参数 退款成功和支付成功都可以使用这个 因为支付宝很方便 退款和支付结果通知的url是一样的校验方式也一样
     * @param notifyParams 这个是request.getParameterMap产生的
     * @return
     */
    NotifyCheckResult checkZfbAllNotify(Map<String,String[]> notifyParams);

    /***************************下边为微信校验 很不方便*************************************/
    /**
     * 校验微信H5支付成功回调参数
     * @param recStr 这个是request.getInputStream()转为String
     * @return
     */
    NotifyCheckResult checkWxH5PayNotify(String recStr);

    /**
     * 校验微信H5退款回调 注意看：当你在看到这个文件夹的时候一定要读我
     * @param recStr 这个是request.getInputStream()转为String
     * @return
     */
    NotifyCheckResult checkWxH5RefundNotify(String recStr);

    /**
     * 校验微信App支付成功回调参数
     * @param recStr 这个是request.getInputStream()转为String
     * @return
     */
    NotifyCheckResult checkWxAppPayNotify(String recStr);

    /**
     * 校验微信App退款回调 注意看：当你在看到这个文件夹的时候一定要读我
     * @param recStr 这个是request.getInputStream()转为String
     * @return
     */
    NotifyCheckResult checkWxAppRefundNotify(String recStr);
}
