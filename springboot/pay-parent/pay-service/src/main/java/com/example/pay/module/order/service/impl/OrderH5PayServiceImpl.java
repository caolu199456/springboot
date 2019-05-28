package com.example.pay.module.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.example.pay.module.order.constants.OrderConstants;
import com.example.pay.module.order.dto.OrderDto;
import com.example.pay.module.order.dto.request.OrderRequestParams;
import com.example.pay.module.order.dto.response.OrderCreatedResponseDto;
import com.example.pay.module.order.service.OrderH5PayService;
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
public class OrderH5PayServiceImpl implements OrderH5PayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderH5PayService.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayClient alipayClient;

    @Resource(name = "wxH5Pay")
    private WXPayConfig wxPayConfig;


    @Override
    public OrderCreatedResponseDto createZfbOrder(OrderRequestParams orderRequestParams) {

        LOGGER.info("收到支付宝H5下单数据:{}",JSON.toJSONString(orderRequestParams));

        //初始化订单
        orderRequestParams.setPayType(OrderConstants.PayType.ZFB.getPayTypeValue());

        OrderDto orderDto = orderService.initOrderInfo(orderRequestParams);
        orderDto.setTerminalType(OrderConstants.TerminalType.H5.getTerminalTypeValue());


        //创建API对应的request
        AlipayTradeCreateRequest alipayRequest = new AlipayTradeCreateRequest();
        //在公共参数中设置回跳和通知地址
        alipayRequest.setNotifyUrl(orderDto.getNotifyUrl());

        Map<String, Object> bizParams = new HashMap<>();
        bizParams.put("out_trade_no", orderDto.getOutTradeNo());
        bizParams.put("total_amount", orderDto.getSpecialPrice());
        bizParams.put("subject", orderDto.getProductName());
        bizParams.put("buyer_id", orderDto.getBuyerId());
        alipayRequest.setBizContent(JSON.toJSONString(bizParams));//填充业务参数
        OrderCreatedResponseDto orderCreatedResponseDto = new OrderCreatedResponseDto();
        try {
            //调用SDK生成表单
            AlipayTradeCreateResponse response = alipayClient.execute(alipayRequest);
            if (response.isSuccess()) {
                orderService.save(orderDto);
                Map<String,String> zfbMap = new HashMap<>();
                zfbMap.put("tradeNo", response.getTradeNo());
                orderCreatedResponseDto.setZfbMap(zfbMap);

                LOGGER.debug("返回的数据为:{}",JSON.toJSONString(orderCreatedResponseDto));

                return orderCreatedResponseDto;
            }
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝返回下单抛出异常", e);
        }
        return null;
    }



    @Override
    public OrderCreatedResponseDto createWxOrder(OrderRequestParams orderRequestParams) {

        LOGGER.info("收到微信H5下单数据:{}",JSON.toJSONString(orderRequestParams));

        //初始化订单
        orderRequestParams.setPayType(OrderConstants.PayType.WX.getPayTypeValue());

        OrderDto orderDto = orderService.initOrderInfo(orderRequestParams);
        orderDto.setTerminalType(OrderConstants.TerminalType.H5.getTerminalTypeValue());

        try {
            //md5签名
            WXPay wxPay = new WXPay(wxPayConfig, null, true, true);

            Map<String, String> params = new HashMap<>();
            params.put("out_trade_no", orderDto.getOutTradeNo());
            params.put("total_fee", orderDto.getSpecialPrice().multiply(new BigDecimal("100")).intValue() + "");
            params.put("body", orderDto.getProductName());
            params.put("spbill_create_ip", orderDto.getIpAddress());
            params.put("notify_url", orderDto.getNotifyUrl());
            params.put("trade_type", "JSAPI");
            params.put("openid", orderRequestParams.getOpenId());

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
                 *    <openid><![CDATA[oUpF8uMuAJO_M2pxb1Q9zNjWeS6o]]></openid>
                 *    <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
                 *    <result_code><![CDATA[SUCCESS]]></result_code>
                 *    <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
                 *    <trade_type><![CDATA[JSAPI]]></trade_type>
                 * </xml>
                 */
                Map<String, String> result = new HashMap<>();
                result.put("appId", responseMap.get("appid"));
                result.put("timeStamp",  WXPayUtil.getCurrentTimestamp()+"");
                result.put("nonceStr", WXPayUtil.generateNonceStr());
                result.put("package", "prepay_id=" + responseMap.get("prepay_id"));
                result.put("signType", WXPayConstants.SignType.MD5.name());
                result.put("paySign", WXPayUtil.generateSignature(result, wxPayConfig.getKey()));

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
