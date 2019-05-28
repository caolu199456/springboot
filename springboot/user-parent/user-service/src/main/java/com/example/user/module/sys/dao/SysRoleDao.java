package com.example.user.module.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.module.sys.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 *@date 2019-04-01 22:52:08
 */
@Mapper
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {
    /**
   * 查询列表
   */
    List<SysRoleEntity> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> params);
}
