package com.example.pay.module.order.service;


import com.example.pay.module.order.dto.OrderDto;
import com.example.pay.module.order.dto.request.OrderRequestParams;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

/**
 * 订单相关
 * @author caolu
 * @date 2019-04-12 17:09:05
 */
public interface OrderService {


    /**
     * 生成25不重复订单号
     * @return
     */
    String genOrderNo();
    /**
     * 根据前端传入的请求参数组装一个完整的订单
     * @param orderRequestParams
     * @return
     */
    OrderDto initOrderInfo(OrderRequestParams orderRequestParams);
    /**
    * 查询列表
    */
    List<OrderDto> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> map);

    /**
    * 存入一条记录
    * @param dto
    */
    void save(OrderDto dto);

    /**
    * 根据id更新一条记录
    * @param dto
    */
    void updateById(OrderDto dto);

    /**
     * 根据商户号查询
     * @param outTradeNo
     */
    OrderDto selectByOutTradeNo(Serializable outTradeNo);

    /**
    * 根据id查询一条记录
    * @param id
    */
    OrderDto selectById(Serializable id);

    /**
    * 根据id删除一条记录
    * @param id
    */
    void deleteById(Serializable id);

    /**
   * 批量删除
   * @param ids
   */
    void deleteBatch(Serializable[] ids);


}
