package com.example.pay.module.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pay.constants.RedisContants;
import com.example.pay.module.order.constants.OrderConstants;
import com.example.pay.module.order.constants.OrderRefundConstants;
import com.example.pay.module.order.dao.OrderRefundDao;
import com.example.pay.module.order.dto.OrderDto;
import com.example.pay.module.order.dto.OrderRefundDto;
import com.example.pay.module.order.entity.OrderRefundEntity;
import com.example.pay.module.order.service.OrderRefundService;
import com.example.pay.module.order.service.OrderService;
import com.example.pay.sdk.wx.WXPay;
import com.example.pay.sdk.wx.WXPayConfig;
import com.example.pay.sdk.wx.WXPayConstants;
import com.example.pay.sdk.wx.WXPayUtil;
import com.example.util.common.CopyUtils;
import com.example.util.kit.SnowFlake;
import com.example.util.redis.RedisUtils;
import com.example.util.redis.model.RLock;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OrderRefundServiceImpl implements OrderRefundService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRefundService.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayClient alipayClient;

    @Resource(name = "wxH5Pay")
    private WXPayConfig wxH5PayConfig;
    @Resource(name = "wxAppPay")
    private WXPayConfig wxAppPayConfig;


    @Override
    public boolean sendRefundRequest(String outTradeNo, String outRefundNo, BigDecimal refundAmount, Byte refundType, String refundNotifyUrl, String account) {
        if (StringUtils.isBlank(outTradeNo)) {
            throw new RuntimeException("退款必须传入原订单号");
        }
        if (StringUtils.isBlank(outRefundNo)) {
            throw new RuntimeException("退款必须传入退款订单号");
        }
        if (refundAmount == null || refundAmount.compareTo(new BigDecimal("0")) <= 0) {
            throw new RuntimeException("退款金额必须不为空 大于0且小于订单总金额");
        }
        if (refundType == null) {
            throw new RuntimeException("退款类型必须传入");
        }

        LOGGER.info("收到商户订单号为:{} 退款金额为{} 退款回调地址为:{}的退款请求", outTradeNo, refundAmount.toPlainString(), refundNotifyUrl);

        RLock rLock = null;
        try {
            //需要加锁不然重复调用产生多笔退款订单
            rLock = RedisUtils.tryLock(RedisContants.ORDER_REFUND_KEY + outTradeNo, 30, TimeUnit.SECONDS);
            if (rLock.isGetLock()) {
                OrderDto orderDto = orderService.selectByOutTradeNo(outTradeNo);

                LOGGER.debug("退款请求的订单数据为:{}", JSON.toJSONString(orderDto));

                if (orderDto == null) {
                    return false;
                }
                if (OrderConstants.Status.INIT.getStatusValue().equals(orderDto.getStatus())) {
                    //非支付成功的订单不允许退款
                    return false;
                }
                //初始化退款订单信息
                OrderRefundDto orderRefundDto = initAndSaveRefundOrder(orderDto, outRefundNo, refundAmount, refundType, refundNotifyUrl, account);

                if (OrderConstants.PayType.ZFB.getPayTypeValue().equals(orderDto.getPayType())) {
                    //支付宝
                    return sendZfbRefundRequest(orderRefundDto);
                }

                if (OrderConstants.PayType.WX.getPayTypeValue().equals(orderDto.getPayType())) {
                    if (StringUtils.isBlank(refundNotifyUrl)) {
                        throw new RuntimeException("微信退款必须指定退款回调地址");
                    }
                    //微信
                    if (OrderConstants.TerminalType.H5.getTerminalTypeValue().equals(orderDto.getTerminalType())) {
                        //微信H5
                        return sendWxH5RefundRequest(orderRefundDto);
                    }
                    if (OrderConstants.TerminalType.APP.getTerminalTypeValue().equals(orderDto.getTerminalType())) {
                        //微信APP
                        return sendWxAppRefundRequest(orderRefundDto);
                    }
                }
            }
        } finally {
            if (rLock != null) {
                rLock.unlock();
            }
        }
        return false;

    }

    private boolean sendZfbRefundRequest(OrderRefundDto orderRefundDto) {

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        Map<String, Object> params = new HashMap<>();
        params.put("out_trade_no", orderRefundDto.getOutTradeNo());
        params.put("refund_amount", orderRefundDto.getRefundAmount());
        params.put("out_request_no", orderRefundDto.getOutRefundNo());
        request.setBizContent(JSON.toJSONString(params));
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);

            LOGGER.info("支付退款返回信息为：{}", JSON.toJSONString(response));

            if (response.isSuccess() && response.getGmtRefundPay() != null) {
                signRefundSuccess(orderRefundDto.getOutRefundNo(), response.getGmtRefundPay());
                LOGGER.info("支付宝订单:{}退款请求已发送 并且退款成功,退款订单号为:{}", orderRefundDto.getOutTradeNo(), orderRefundDto.getOutRefundNo());
                return true;
            } else {
                //失败的订单不需要存入
                deleteById(orderRefundDto.getId());
                LOGGER.info("支付宝订单:{}退款请求发送失败 失败信息为:{}", orderRefundDto.getOutTradeNo(), response.getBody());
            }
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝发起退款失败", e);
        }
        return false;
    }

    private boolean sendWxH5RefundRequest(OrderRefundDto orderRefundDto) {
        try {
            //md5签名
            WXPay wxPay = new WXPay(wxH5PayConfig, null, true, true);

            Map<String, String> params = new HashMap<>();
            params.put("out_trade_no", orderRefundDto.getOutTradeNo());
            params.put("out_refund_no", orderRefundDto.getOutRefundNo());
            params.put("total_fee", orderRefundDto.getTotalAmount().multiply(new BigDecimal("100")).intValue() + "");
            params.put("refund_fee", orderRefundDto.getRefundAmount().multiply(new BigDecimal("100")).intValue() + "");
            params.put("notify_url", orderRefundDto.getRefundNotifyUrl());
            Map<String, String> requestData = wxPay.fillRequestData(params);

            String withoutCert = wxPay.requestWithCert(WXPayConstants.REFUND_URL_SUFFIX, requestData, 10000, 10000);
            Map<String, String> responseMap = WXPayUtil.xmlToMap(withoutCert);

            LOGGER.info("微信H5退款返回信息为：{}", JSON.toJSONString(responseMap));

            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                LOGGER.info("微信H5订单:{}退款请求已发送,退款订单号为:{}", orderRefundDto.getOutTradeNo(), orderRefundDto.getOutRefundNo());
                return true;
            }else {
                //失败的退款订单不需要存入
                deleteById(orderRefundDto.getId());
                LOGGER.info("微信H5订单:{}退款请求发送失败 失败信息为:{}", orderRefundDto.getOutTradeNo(),JSON.toJSONString(responseMap));
            }
        } catch (Exception e) {
            LOGGER.error("微信H5发起退款失败", e);
        }
        return false;
    }


    private boolean sendWxAppRefundRequest(OrderRefundDto orderRefundDto) {

        try {
            //md5签名
            WXPay wxPay = new WXPay(wxAppPayConfig, null, true, true);

            Map<String, String> params = new HashMap<>();
            params.put("out_trade_no", orderRefundDto.getOutTradeNo());
            params.put("out_refund_no", orderRefundDto.getOutRefundNo());
            params.put("total_fee", orderRefundDto.getTotalAmount().multiply(new BigDecimal("100")).intValue() + "");
            params.put("refund_fee", orderRefundDto.getRefundAmount().multiply(new BigDecimal("100")).intValue() + "");
            params.put("notify_url", orderRefundDto.getRefundNotifyUrl());

            Map<String, String> requestData = wxPay.fillRequestData(params);

            String withoutCert = wxPay.requestWithoutCert(WXPayConstants.REFUND_URL_SUFFIX, requestData, 10000, 10000);
            Map<String, String> responseMap = WXPayUtil.xmlToMap(withoutCert);

            LOGGER.info("微信APP退款返回信息为：{}", JSON.toJSONString(responseMap));

            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                LOGGER.info("微信APP订单:{}退款请求已发送,退款订单号为:{}",  orderRefundDto.getOutTradeNo(), orderRefundDto.getOutRefundNo());
                return true;
            }else {
                //失败的退款订单不需要存入
                deleteById(orderRefundDto.getId());
                LOGGER.info("微信App订单:{}退款请求发送失败 失败信息为:{}", orderRefundDto.getOutTradeNo(),JSON.toJSONString(responseMap));
            }
        } catch (Exception e) {
            LOGGER.error("微信APP发起退款失败", e);
        }
        return false;
    }

    /**
     *
     * @param orderDto 订单信息
     * @param outRefundNo 退款订单号
     * @param refundAmount 退款金额
     * @param refundType 1自动退款2手动退款
     * @param refundNotifyUrl 回调地址 支付宝不需要
     * @param account 退款账号
     * @return
     */
    private OrderRefundDto initAndSaveRefundOrder(OrderDto orderDto, String outRefundNo, BigDecimal refundAmount, Byte refundType, String refundNotifyUrl, String account) {
        OrderRefundDto orderRefundDto = selectByOutRefundNo(outRefundNo);
        Date date = new Date();
        if (orderRefundDto == null) {
            //存入退款订单号
            orderRefundDto = new OrderRefundDto();
            orderRefundDto.setId(SnowFlake.nextId());
            orderRefundDto.setOrderId(orderDto.getId());
            orderRefundDto.setOutTradeNo(orderDto.getOutTradeNo());
            orderRefundDto.setTotalAmount(orderDto.getSpecialPrice());
            orderRefundDto.setOutRefundNo(outRefundNo);
            orderRefundDto.setRefundAmount(refundAmount);
            orderRefundDto.setRefundStatus(OrderRefundConstants.OrderRefundStatus.REFUNDING.getOrderRefundStatusValue());
            //微信需要单独设置url
            orderRefundDto.setRefundNotifyUrl(refundNotifyUrl);
            orderRefundDto.setRefundType(refundType);
            orderRefundDto.setCreator(account);
            orderRefundDto.setCreateTime(date);
            orderRefundDto.setEditor(account);
            orderRefundDto.setEditTime(date);
            Long id = save(orderRefundDto);
            orderRefundDto.setId(id);
        }else {
            OrderRefundDto updateObj = new OrderRefundDto();
            updateObj.setId(orderRefundDto.getId());
            updateObj.setEditor(account);
            updateObj.setEditTime(date);
            updateObj.setRefundStatus(OrderRefundConstants.OrderRefundStatus.REFUNDING.getOrderRefundStatusValue());
            updateById(updateObj);
        }
        return orderRefundDto;
    }


    @Autowired
    private OrderRefundDao orderRefundDao;


    @Override
    public List<OrderRefundDto> queryList(Map<String, Object> params) {
        List<OrderRefundEntity> list = orderRefundDao.queryList(params);
        return CopyUtils.copyList(list, OrderRefundDto.class);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return orderRefundDao.queryTotal(params);
    }

    @Override
    public Long save(OrderRefundDto dto) {
        OrderRefundEntity entity = CopyUtils.copyObj(dto, OrderRefundEntity.class);
        entity.setId(SnowFlake.nextId());
        orderRefundDao.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateById(OrderRefundDto dto) {
        OrderRefundEntity entity = CopyUtils.copyObj(dto, OrderRefundEntity.class);
        orderRefundDao.updateById(entity);
    }

    @Override
    public OrderRefundDto selectById(Serializable id) {
        OrderRefundEntity entity = orderRefundDao.selectById(id);
        return CopyUtils.copyObj(entity, OrderRefundDto.class);
    }

    @Override
    public OrderRefundDto selectByOutRefundNo(String outRefundNo) {
        OrderRefundEntity entity = orderRefundDao.selectOne(new QueryWrapper<OrderRefundEntity>().eq("out_refund_no", outRefundNo));
        return CopyUtils.copyObj(entity, OrderRefundDto.class);
    }

    @Override
    public void deleteById(Serializable id) {
        orderRefundDao.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(Serializable[] ids) {
        for (Serializable id : ids) {
            deleteById(id);
        }
    }

    @Override
    @Transactional
    public void signRefundSuccess(String outRefundNo,Date refundSuccessTime) {
        //退款成功
        OrderRefundDto orderRefundDto = selectByOutRefundNo(outRefundNo);
        if (orderRefundDto == null) {
            throw new RuntimeException("退款订单不存在");
        }
        RLock rLock = null;
        try {
            rLock = RedisUtils.tryLock("SIGN_REFUND_SUCCESS_LOCK:" + orderRefundDto.getOrderId(), 1, TimeUnit.MINUTES);
            if (rLock.isGetLock()) {
                //退款成功 针对微信H5
                if (orderRefundDto != null && OrderRefundConstants.OrderRefundStatus.REFUNDING.getOrderRefundStatusValue().equals(orderRefundDto.getRefundStatus())) {
                    //只有在退款中的我们才更新
                    OrderRefundDto updateObj = new OrderRefundDto();
                    updateObj.setId(orderRefundDto.getId());
                    updateObj.setRefundSuccessTime(refundSuccessTime);
                    updateObj.setEditTime(new Date());
                    updateObj.setRefundStatus(OrderRefundConstants.OrderRefundStatus.REFUND_SUCCESS.getOrderRefundStatusValue());
                    updateById(updateObj);

                    //然后标记订单金额
                    OrderDto orderDto = orderService.selectById(orderRefundDto.getOrderId());
                    if (orderDto != null) {
                        OrderDto updateOrderObj = new OrderDto();
                        updateOrderObj.setId(orderDto.getId());

                        BigDecimal totalRefunFee = orderDto.getTotalRefundFee() == null ? new BigDecimal("0") : orderDto.getTotalRefundFee();
                        updateOrderObj.setTotalRefundFee(totalRefunFee.add(orderRefundDto.getRefundAmount()));

                        orderService.updateById(updateOrderObj);
                    }
                }
            } else {
                throw new RuntimeException("订单退款成功 但没有更新数据库,抛出异常使第三方平台继续回调");
            }
        } finally {
            rLock.unlock();
        }
    }
}
