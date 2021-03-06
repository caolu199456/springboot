package com.example.user.module.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.module.sys.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 *@date 2019-04-01 13:06:23
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUserEntity> {
    /**
   * 查询列表
   */
    List<SysUserEntity> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> params);
}
