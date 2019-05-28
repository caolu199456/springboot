package com.example.user.module.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.module.sys.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * @author caolu
 *@date 2019-04-02 09:42:44
 */
@Mapper
public interface SysMenuDao extends BaseMapper<SysMenuEntity> {
    /**
   * 查询列表
   */
    List<SysMenuEntity> queryList();

    Integer queryMaxSortByPid(Long parentId);
}
