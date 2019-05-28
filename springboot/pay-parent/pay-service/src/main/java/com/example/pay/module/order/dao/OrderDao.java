package com.example.pay.module.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pay.module.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;
/**
 *
 * @author caolu
 *@date 2019-04-12 17:09:04
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
    /**
   * 查询列表
   */
    List<OrderEntity> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> params);
}
