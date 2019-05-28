package com.example.user.module.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.module.sys.entity.SysMenuEntity;
import com.example.user.module.sys.entity.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 *@date 2019-04-03 23:11:18
 */
@Mapper
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenuEntity> {
    /**
   * 查询列表
   */
    List<SysRoleMenuEntity> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> params);

    List<SysMenuEntity> selectByRoleId(Serializable roleId);
}
