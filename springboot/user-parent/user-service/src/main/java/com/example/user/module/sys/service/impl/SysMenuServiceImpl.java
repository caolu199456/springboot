package com.example.user.module.sys.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.user.module.sys.dao.SysMenuDao;
import com.example.user.module.sys.dto.SysMenuDto;
import com.example.user.module.sys.entity.SysMenuEntity;
import com.example.user.module.sys.service.SysMenuService;
import com.example.util.common.CopyUtils;
import com.example.util.kit.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author caolu
 * @date 2019-04-02 09:42:45
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuDao sysMenuDao;


    @Override
    public List<SysMenuDto> queryList(){
        List<SysMenuEntity> list = sysMenuDao.queryList();
        return CopyUtils.copyList(list, SysMenuDto.class);
    }


    @Override
    public void save(SysMenuDto dto){
        SysMenuEntity entity = CopyUtils.copyObj(dto, SysMenuEntity.class);
        entity.setId(SnowFlake.nextId());
        entity.setSort(sysMenuDao.queryMaxSortByPid(dto.getParentId()) + 1);
        sysMenuDao.insert(entity);
    }

    @Override
    public void updateById(SysMenuDto dto){
        SysMenuEntity entity = CopyUtils.copyObj(dto, SysMenuEntity.class);
        sysMenuDao.updateById(entity);
    }

    @Override
    public SysMenuDto selectById(Serializable id){
        SysMenuEntity entity = sysMenuDao.selectById(id);
        return CopyUtils.copyObj(entity, SysMenuDto.class);
    }

    @Override
    public void deleteById(Serializable id){
        sysMenuDao.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(Serializable[] ids){
        for (Serializable id : ids) {
            deleteById(id);
        }
    }

    @Override
    public List<SysMenuDto> queryCanUsefulMenus() {
        List<SysMenuEntity> entityList = sysMenuDao.selectList(new QueryWrapper<SysMenuEntity>().eq("status", 1));
        return CopyUtils.copyList(entityList, SysMenuDto.class);
    }
}
