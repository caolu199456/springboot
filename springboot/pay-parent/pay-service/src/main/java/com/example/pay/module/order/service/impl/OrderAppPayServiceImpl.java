package com.example.pay.module.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.example.pay.module.order.constants.OrderConstants;
import com.example.pay.module.order.dto.OrderDto;
import com.example.pay.module.order.dto.request.OrderRequestParams;
import com.example.pay.module.order.dto.response.OrderCreatedResponseDto;
import com.example.pay.module.order.service.OrderAppPayService;
import com.example.pay.module.order.service.OrderService;
import com.example.pay.sdk.wx.WXPay;
import com.example.pay.sdk.wx.WXPayConfig;
import com.example.pay.sdk.wx.WXPayConstants;
import com.example.pay.sdk.wx.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderAppPayServiceImpl implements OrderAppPayService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderAppPayService.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayClient alipayClient;

    @Resource(name = "wxAppPay")
    private WXPayConfig wxPayConfig;


    @Override
    public OrderCreatedResponseDto createZfbOrder(OrderRequestParams orderRequestParams) {

        LOGGER.info("收到支付宝APP下单数据:{}", JSON.toJSONString(orderRequestParams));

        //初始化订单
        orderRequestParams.setPayType(OrderConstants.PayType.ZFB.getPayTypeValue());
        OrderDto orderDto = orderService.initOrderInfo(orderRequestParams);
        orderDto.setTerminalType(OrderConstants.TerminalType.APP.getTerminalTypeValue());



        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();


//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setSubject(orderDto.getProductName());
        model.setOutTradeNo(orderDto.getOutTradeNo());
        model.setTotalAmount(orderDto.getSpecialPrice().toPlainString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(orderDto.getNotifyUrl());

        OrderCreatedResponseDto orderCreatedResponseDto = new OrderCreatedResponseDto();
        try {
            //调用SDK生成表单
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if (response.isSuccess()) {

                orderService.save(orderDto);
                LOGGER.debug("返回的数据为:{}",JSON.toJSONString(orderCreatedResponseDto));
                Map<String, String> zfbMap = new HashMap<>();
                zfbMap.put("orderInfo", response.getBody());
                return orderCreatedResponseDto;
            }

        } catch (AlipayApiException e) {
            LOGGER.error("支付宝返回下单抛出异常", e);
        }
        return null;
    }

    @Override
    public OrderCreatedResponseDto createWxOrder(OrderRequestParams orderRequestParams) {

        LOGGER.info("收到微信APP下单数据:{}",JSON.toJSONString(orderRequestParams));

        //初始化订单
        orderRequestParams.setPayType(OrderConstants.PayType.WX.getPayTypeValue());
        OrderDto orderDto = orderService.initOrderInfo(orderRequestParams);
        orderDto.setTerminalType(OrderConstants.TerminalType.APP.getTerminalTypeValue());

        try {
            //md5签名
            WXPay wxPay = new WXPay(wxPayConfig, null, true, true);

            Map<String, String> params = new HashMap<>();
            params.put("out_trade_no", orderDto.getOutTradeNo());
            params.put("total_fee", orderDto.getSpecialPrice().multiply(new BigDecimal("100")).intValue() + "");
            params.put("body", orderDto.getProductName());
            params.put("spbill_create_ip", orderDto.getIpAddress());
            params.put("notify_url", orderDto.getNotifyUrl());
            params.put("trade_type", "APP");

            Map<String, String> requestData = wxPay.fillRequestData(params);

            String withoutCert = wxPay.requestWithoutCert(WXPayConstants.UNIFIEDORDER_URL_SUFFIX, requestData, 10000, 10000);
            Map<String, String> responseMap = WXPayUtil.xmlToMap(withoutCert);
            if (responseMap.containsKey("return_code")
                    && "SUCCESS".equals(responseMap.get("return_code"))) {

                orderService.save(orderDto);

                /*
                 * <xml>
                 *    <return_code><![CDATA[SUCCESS]]></return_code>
                 *    <return_msg><![CDATA[OK]]></return_msg>
                 *    <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
                 *    <mch_id><![CDATA[10000100]]></mch_id>
                 *    <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>
                 *    <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
                 *    <result_code><![CDATA[SUCCESS]]></result_code>
                 *    <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
                 *    <trade_type><![CDATA[APP]]></trade_type>
                 * </xml>
                 */

                Map<String, String> result = new HashMap<>();
                result.put("appId", responseMap.get("appid"));
                result.put("mchId", responseMap.get("mch_id"));
                result.put("prepayId", responseMap.get("prepay_id"));
                result.put("packageValue", "Sign=WXPay");
                result.put("timeStamp", WXPayUtil.getCurrentTimestamp()+"");
                result.put("sign", WXPayUtil.generateSignature(result, wxPayConfig.getKey()));

                OrderCreatedResponseDto orderCreatedResponseDto = new OrderCreatedResponseDto();
                orderCreatedResponseDto.setWxMap(result);

                LOGGER.debug("返回的数据为:{}",JSON.toJSONString(orderCreatedResponseDto));

                return orderCreatedResponseDto;
            }
        } catch (Exception e) {
            LOGGER.error("微信H5发起支付失败",e);
        }
        return null;
    }
}
