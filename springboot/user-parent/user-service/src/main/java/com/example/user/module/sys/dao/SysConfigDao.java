package com.example.user.module.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.module.sys.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 *@date 2019-04-18 09:43:43
 */
@Mapper
public interface SysConfigDao extends BaseMapper<SysConfigEntity> {
    /**
   * 查询列表
   */
    List<SysConfigEntity> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> params);
}
