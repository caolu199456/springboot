package com.example.mobile.controller.h5.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.example.mobile.constants.RedisCacheConstants;
import com.example.mobile.controller.BaseController;
import com.example.pay.module.common.dto.NotifyCheckResult;
import com.example.pay.module.common.service.NotifyCheckService;
import com.example.pay.module.order.constants.OrderConstants;
import com.example.pay.module.order.dto.OrderDto;
import com.example.pay.module.order.dto.request.OrderRequestParams;
import com.example.pay.module.order.dto.response.OrderCreatedResponseDto;
import com.example.pay.module.order.service.OrderH5PayService;
import com.example.pay.module.order.service.OrderRefundService;
import com.example.pay.module.order.service.OrderService;
import com.example.user.module.sys.service.SysConfigService;
import com.example.util.common.Response;
import com.example.util.http.RequestUtils;
import com.example.util.redis.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("h5/orderH5Pay")
@Api(description ="h5用户支付相关")
public class OrderH5PayController extends BaseController {
    @Reference
    private OrderH5PayService orderH5PayService;
    @Reference
    private OrderService orderService;
    @Reference
    private OrderRefundService orderRefundService;
    @Reference
    private NotifyCheckService notifyCheckService;
    @Reference
    private SysConfigService sysConfigService;



    @Value("${pay.notify-url-prefix}")
    private String notifyUrlPrefix;

    @PostMapping("createZfbOrder")
    @ApiOperation("创建支付宝订单")
    public Response<OrderCreatedResponseDto> createZfbOrder(@RequestBody OrderRequestParams orderRequestParams) {
        initOrder(orderRequestParams);
        orderRequestParams.setNotifyUrl(notifyUrlPrefix + "h5/orderH5Pay/h5PayNotify/" + OrderConstants.PayType.ZFB.getPayTypeValue());
        return Response.ok().wrap(orderH5PayService.createZfbOrder(orderRequestParams));
    }

    @PostMapping("createWxOrder")
    @ApiOperation("创建微信订单")
    public Response<OrderCreatedResponseDto> createWxOrder(@RequestBody OrderRequestParams orderRequestParams) {
        initOrder(orderRequestParams);
        orderRequestParams.setNotifyUrl(notifyUrlPrefix + "h5/orderH5Pay/h5PayNotify/" + OrderConstants.PayType.WX.getPayTypeValue());
        return Response.ok().wrap(orderH5PayService.createWxOrder(orderRequestParams));
    }

    private void initOrder(OrderRequestParams orderRequestParams) {

        orderRequestParams.setOpenId(getH5MemberInfo().getBuyerId());
        orderRequestParams.setBuyerId(getH5MemberInfo().getBuyerId());
        orderRequestParams.setIpAddress(RequestUtils.getRealIp(request));
        //押金
        orderRequestParams.setOriginalPrice(new BigDecimal("0.01"));
        orderRequestParams.setSpecialPrice(new BigDecimal("0.01"));

    }


    @PostMapping("wxH5RefundNotify")
    @ApiOperation("微信H5退款成功回调")
    public void wxRefundNotify(HttpServletRequest req, HttpServletResponse response) {

        NotifyCheckResult notifyCheckResult = null;
        try {
            ServletInputStream recIn = req.getInputStream();
            String wxRecData = new String(IOUtils.toByteArray(recIn));
            logger.info("微信H5退款回调原始数据:{}", wxRecData);
            notifyCheckResult = notifyCheckService.checkWxH5RefundNotify(wxRecData);
        } catch (IOException e) {
            logger.error("微信H5支付成功回掉参数解析错误", e);
        }
        logger.info("微信H5退款交易成功参数解析:{}", JSON.toJSONString(notifyCheckResult));
        if (notifyCheckResult == null || !notifyCheckResult.isCheckSuccess()) {
            return;
        }
        Byte tradeStatusType = notifyCheckResult.getTradeStatusType();

        try {
            if(new Byte("2").equals(tradeStatusType)) {
                if (!notifyCheckResult.isRefundSuccess()) {
                    return;
                }
                System.err.println(notifyCheckResult.getOutRefundNo());
                orderRefundService.signRefundSuccess(notifyCheckResult.getOutRefundNo(),notifyCheckResult.getRefundSuccessTime());
            }
            writeResponseInfo(OrderConstants.PayType.WX.getPayTypeValue(), true, response);
        } catch (Exception e) {

            //3回复支付平台
            writeResponseInfo(OrderConstants.PayType.WX.getPayTypeValue(), false, response);
            logger.error("微信H5退款处理业务逻辑报错");
        }
    }

    @PostMapping("h5PayNotify/{payType}")
    @ApiOperation("h5支付回调 包括支付宝退款回调")
    public void h5PayNotify(@PathVariable Byte payType, HttpServletRequest req, HttpServletResponse response) {

        NotifyCheckResult notifyCheckResult = null;
        if (OrderConstants.PayType.ZFB.getPayTypeValue().equals(payType)) {
            //支付宝
            notifyCheckResult = notifyCheckService.checkZfbAllNotify(req.getParameterMap());
        } else if (OrderConstants.PayType.WX.getPayTypeValue().equals(payType)) {
            //微信
            try {
                ServletInputStream recIn = req.getInputStream();
                String wxRecData = new String(IOUtils.toByteArray(recIn));
                notifyCheckResult = notifyCheckService.checkWxH5PayNotify(wxRecData);
            } catch (IOException e) {
                logger.error("微信支付成功回掉参数解析错误", e);
            }
        }
        logger.info("交易成功参数解析:{}", JSON.toJSONString(notifyCheckResult));
        if (notifyCheckResult == null || !notifyCheckResult.isCheckSuccess()) {
            return;
        }
        Byte tradeStatusType = notifyCheckResult.getTradeStatusType();
        String outTradeNo = notifyCheckResult.getOutTradeNo();

        try {

            //先判断这次的回调类型是退款还是什么
            if (new Byte("1").equals(tradeStatusType)) {
                if (!notifyCheckResult.isPaySuccess()) {
                    return;
                }

                //1加锁
                boolean isGetLock = RedisUtils.setnx(RedisCacheConstants.PAY_SUCCESS_ORDER + outTradeNo, "1", 1, TimeUnit.MINUTES);
                if (!isGetLock) {
                    return;
                }
                //2处理其他逻辑


                OrderDto orderDto = orderService.selectByOutTradeNo(outTradeNo);
                //然后判断订单状态
                if (OrderConstants.Status.INIT.getStatusValue().equals(orderDto.getStatus())) {

                    //只有在初始订单才能处理 然后写我们的逻辑

                    //doSomethings


                    //最后更新订单状态
                    OrderDto updateObj = new OrderDto();
                    updateObj.setId(orderDto.getId());
                    updateObj.setTradeNo(notifyCheckResult.getTradeNo());
                    updateObj.setPayTime(notifyCheckResult.getPaySuccessTime());
                    updateObj.setUpdateTime(new Date());
                    updateObj.setStatus(OrderConstants.Status.PAY_SUCCESS.getStatusValue());
                    orderService.updateById(updateObj);
                }

            }

            //3回复支付平台
            writeResponseInfo(payType, true, response);

            //4解除锁
            RedisUtils.del(RedisCacheConstants.PAY_SUCCESS_ORDER + outTradeNo);

        } catch (Exception e) {

            //3回复支付平台
            writeResponseInfo(payType, false, response);

            //解除锁
            RedisUtils.del(RedisCacheConstants.PAY_SUCCESS_ORDER + outTradeNo);
            logger.error("交易成功处理业务逻辑报错");
        }
    }

    private void writeResponseInfo(Byte payType, boolean isHandSuccess, HttpServletResponse response) {
        try {
            ServletOutputStream out = response.getOutputStream();
            if (OrderConstants.PayType.ZFB.getPayTypeValue().equals(payType)) {
                //支付宝
                if (isHandSuccess) {
                    out.print("SUCCESS");
                    out.flush();
                } else {
                    out.print("FAIL");
                    out.flush();
                }
            } else if (OrderConstants.PayType.WX.getPayTypeValue().equals(payType)) {
                //微信
                if (isHandSuccess) {
                    String xml = "<xml>\n" +
                            "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                            "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                            "</xml>";
                    out.print(xml);
                    out.flush();
                } else {
                    String xml = "<xml>\n" +
                            "  <return_code><![CDATA[FAIL]]></return_code>\n" +
                            "  <return_msg><![CDATA[FAIL]]></return_msg>\n" +
                            "</xml>";
                    out.print(xml);
                    out.flush();
                }
            }
        } catch (IOException e) {
            logger.info("交易成功回复第三方平台失败");
        }
    }
}
