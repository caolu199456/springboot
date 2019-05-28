package com.example.user.module.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.module.sys.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 *@date 2019-04-03 23:10:04
 */
@Mapper
public interface SysUserRoleDao extends BaseMapper<SysUserRoleEntity> {
    /**
   * 查询列表
   */
    List<SysUserRoleEntity> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> params);

    List<Long> selectRoleIdsByUserId(Serializable userId);
}
