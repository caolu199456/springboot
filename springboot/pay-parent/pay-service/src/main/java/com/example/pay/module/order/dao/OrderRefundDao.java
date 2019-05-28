package com.example.pay.module.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pay.module.order.entity.OrderRefundEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;
/**
 *
 * @author caolu
 *@date 2019-04-15 15:02:52
 */
@Mapper
public interface OrderRefundDao extends BaseMapper<OrderRefundEntity> {
    /**
   * 查询列表
   */
    List<OrderRefundEntity> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> params);
}
