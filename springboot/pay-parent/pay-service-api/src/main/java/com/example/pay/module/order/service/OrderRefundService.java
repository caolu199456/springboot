package com.example.pay.module.order.service;

import com.example.pay.module.common.dto.NotifyCheckResult;
import com.example.pay.module.order.dto.OrderRefundDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发送退款申请
 */
public interface OrderRefundService {

    /**
     * 标记一条订单退款成功 并且更新原订单的多次退款产生的退款总金额
     * @param outRefundNo 退款订单号
     * @param refundSuccessTime 退款成功时间
     */
    void signRefundSuccess(String outRefundNo, Date refundSuccessTime);

    /**
     * 发起退款申请 如果发起成功转台会扭转为正在退款 如果正在退款中也可以再发起 正在退款再次发起需要传入退款订单号
     *
     * @param outTradeNo 订单号
     * @param outRefundNo 退款订单号 由调用者传入保证幂等性 OrderService.genOrderNo();
     * @param refundAmount
     * @param refundType      @see OrderRefundConstants 1自动结算2人工
     * @param refundNotifyUrl 微信退款需要传入 支付宝传入null即可
     * @param userAccount     操作人 人工退款需要传入
     * @return
     */
    boolean sendRefundRequest(String outTradeNo,
                              String outRefundNo,
                              BigDecimal refundAmount,
                              Byte refundType,
                              String refundNotifyUrl,
                              String userAccount);


    /**
     * 查询列表
     */
    List<OrderRefundDto> queryList(Map<String, Object> params);

    /**
     * 查询数量
     */
    int queryTotal(Map<String, Object> map);

    /**
     * 存入一条记录
     *
     * @param dto
     */
    Long save(OrderRefundDto dto);

    /**
     * 根据id更新一条记录
     *
     * @param dto
     */
    void updateById(OrderRefundDto dto);

    /**
     * 根据id查询一条记录
     *
     * @param id
     */
    OrderRefundDto selectById(Serializable id);

    /**
     * 根据id查询一条记录
     *
     * @param outRefundNo
     */
    OrderRefundDto selectByOutRefundNo(String outRefundNo);

    /**
     * 根据id删除一条记录
     *
     * @param id
     */
    void deleteById(Serializable id);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteBatch(Serializable[] ids);



}
