package com.example.pay.module.order.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pay.module.order.constants.OrderConstants;
import com.example.pay.module.order.dao.OrderDao;
import com.example.pay.module.order.dto.OrderDto;
import com.example.pay.module.order.dto.request.OrderRequestParams;
import com.example.pay.module.order.entity.OrderEntity;
import com.example.pay.module.order.service.OrderService;
import com.example.util.common.CopyUtils;
import com.example.util.kit.OrderUtils;
import com.example.util.kit.SnowFlake;
import com.example.util.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 *
 * @author caolu
 * @date 2019-04-12 17:09:05
 */
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderDao orderDao;

    @Override
    public List<OrderDto> queryList(Map<String,Object> params){
        List<OrderEntity> list = orderDao.queryList(params);
        return CopyUtils.copyList(list,OrderDto.class);
    }

    @Override
    public int queryTotal(Map<String, Object> params){
        return orderDao.queryTotal(params);
    }

    @Override
    public void save(OrderDto dto){
        OrderEntity entity = CopyUtils.copyObj(dto,OrderEntity.class);
        entity.setId(SnowFlake.nextId());
        orderDao.insert(entity);
    }

    @Override
    public void updateById(OrderDto dto){
        OrderEntity entity = CopyUtils.copyObj(dto,OrderEntity.class);
        orderDao.updateById(entity);
    }

    @Override
    public OrderDto selectByOutTradeNo(Serializable outTradeNo) {
        OrderEntity orderEntity = orderDao.selectOne(new QueryWrapper<OrderEntity>().eq("out_trade_no", outTradeNo).last("limit 1"));
        return CopyUtils.copyObj(orderEntity, OrderDto.class);
    }

    @Override
    public OrderDto selectById(Serializable id){
        OrderEntity entity = orderDao.selectById(id);
        return CopyUtils.copyObj(entity,OrderDto.class);
    }

    @Override
    public void deleteById(Serializable id){
        orderDao.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(Serializable[] ids){
        for (Serializable id : ids) {
            deleteById(id);
        }
    }

    @Override
    public String genOrderNo() {
        return OrderUtils.getOrderNo();
    }

    @Override
    public OrderDto initOrderInfo(OrderRequestParams orderRequestParams) {
        Date now = new Date();
        OrderDto orderDto = CopyUtils.copyObj(orderRequestParams, OrderDto.class);

        if (OrderConstants.PayType.ZFB.getPayTypeValue().equals(orderRequestParams.getPayType())) {
            orderDto.setAppId(SpringUtils.getConfigValue("pay.zfb.appId"));
        }else {
            orderDto.setAppId(SpringUtils.getConfigValue("pay.wx.appId"));
        }
        orderDto.setId(SnowFlake.nextId());
        orderDto.setOutTradeNo(OrderUtils.getOrderNo());
        orderDto.setStatus(OrderConstants.Status.INIT.getStatusValue());
        orderDto.setCreateTime(now);
        orderDto.setUpdateTime(now);
        return orderDto;
    }
}
