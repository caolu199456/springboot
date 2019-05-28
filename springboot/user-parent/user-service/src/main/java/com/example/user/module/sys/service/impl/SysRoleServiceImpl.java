package com.example.user.module.sys.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.user.module.sys.dao.SysRoleDao;
import com.example.user.module.sys.dao.SysRoleMenuDao;
import com.example.user.module.sys.dto.SysMenuDto;
import com.example.user.module.sys.dto.SysRoleDto;
import com.example.user.module.sys.entity.SysMenuEntity;
import com.example.user.module.sys.entity.SysRoleEntity;
import com.example.user.module.sys.entity.SysRoleMenuEntity;
import com.example.user.module.sys.service.SysMenuService;
import com.example.user.module.sys.service.SysRoleService;
import com.example.util.common.CopyUtils;
import com.example.util.kit.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author caolu
 * @date 2019-04-01 22:52:09
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public List<SysRoleDto> queryList(Map<String,Object> params){
        List<SysRoleEntity> list = sysRoleDao.queryList(params);
        return CopyUtils.copyList(list, SysRoleDto.class);
    }

    @Override
    public int queryTotal(Map<String, Object> params){
        return sysRoleDao.queryTotal(params);
    }

    @Override
    @Transactional
    public void save(SysRoleDto dto){
        dto.setId(SnowFlake.nextId());
        dto.setStatus((byte) 1);

        SysRoleEntity entity = CopyUtils.copyObj(dto,SysRoleEntity.class);
        sysRoleDao.insert(entity);

        saveRoleMenuRelation(dto);
    }

    @Override
    @Transactional
    public void updateById(SysRoleDto dto){
        SysRoleEntity entity = CopyUtils.copyObj(dto,SysRoleEntity.class);
        sysRoleDao.updateById(entity);
        saveRoleMenuRelation(dto);
    }



    @Override
    public SysRoleDto selectById(Serializable id){
        //用户菜单信息
        List<SysMenuDto> roleMenuList = queryRoleMenus(id);
        SysRoleDto sysRoleDto = CopyUtils.copyObj(sysRoleDao.selectById(id), SysRoleDto.class);
        if (sysRoleDto != null) {
            sysRoleDto.setRoleMenuList(roleMenuList);
        }
        return sysRoleDto;
    }

    @Override
    @Transactional
    public void deleteById(Serializable id){
        sysRoleDao.deleteById(id);
        //删除角色与菜单的关系
        sysRoleMenuDao.delete(new QueryWrapper<SysRoleMenuEntity>().eq("role_id", id));
    }

    @Override
    @Transactional
    public void deleteBatch(Serializable[] ids){
        for (Serializable id : ids) {
            deleteById(id);
        }
    }

    @Override
    public List<SysRoleDto> queryAllRoles() {
        List<SysRoleEntity> sysRoleEntityList = sysRoleDao.selectList(new QueryWrapper<SysRoleEntity>().eq("status", 1));
        return CopyUtils.copyList(sysRoleEntityList, SysRoleDto.class);
    }

    @Override
    public List<SysMenuDto> queryRoleMenus(Serializable roleId) {
        List<SysMenuDto> result = new ArrayList<>();

        //得到所有启用的菜单
        List<SysMenuDto> canUsefulMenus = sysMenuService.queryCanUsefulMenus();
        List<SysMenuEntity> roleMenuList = sysRoleMenuDao.selectByRoleId(roleId);

        if (!CollectionUtils.isEmpty(canUsefulMenus)) {
            for (SysMenuDto canUsefulMenu : canUsefulMenus) {

                SysMenuDto item = CopyUtils.copyObj(canUsefulMenu, SysMenuDto.class);

                if (!CollectionUtils.isEmpty(roleMenuList)) {
                    for (SysMenuEntity roleMenuEntity : roleMenuList) {
                        if (Objects.equals(canUsefulMenu.getId(), roleMenuEntity.getId())) {
                            //说明我拥有菜单树上的菜单
                            item.setChecked(true);
                        }
                    }
                }
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 保存角色和菜单的关系
     * @param dto
     */
    private void saveRoleMenuRelation(SysRoleDto dto) {
        //第一步先清除关系
        sysRoleMenuDao.delete(new QueryWrapper<SysRoleMenuEntity>().eq("role_id", dto.getId()));
        if (CollectionUtils.isEmpty(dto.getRoleMenuList())) {
            return;
        }
        for (SysMenuDto sysMenuDto : dto.getRoleMenuList()) {
            SysRoleMenuEntity sysRoleMenuEntity = new SysRoleMenuEntity();
            sysRoleMenuEntity.setId(SnowFlake.nextId());
            sysRoleMenuEntity.setRoleId(dto.getId());
            sysRoleMenuEntity.setMenuId(sysMenuDto.getId());
            sysRoleMenuDao.insert(sysRoleMenuEntity);
        }
    }
}
