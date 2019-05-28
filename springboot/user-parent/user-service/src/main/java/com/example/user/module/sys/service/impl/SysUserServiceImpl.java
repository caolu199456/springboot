package com.example.user.module.sys.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.user.module.sys.dao.SysRoleDao;
import com.example.user.module.sys.dao.SysUserDao;
import com.example.user.module.sys.dao.SysUserRoleDao;
import com.example.user.module.sys.dto.SysRoleDto;
import com.example.user.module.sys.dto.SysUserDto;
import com.example.user.module.sys.entity.SysRoleEntity;
import com.example.user.module.sys.entity.SysUserEntity;
import com.example.user.module.sys.entity.SysUserRoleEntity;
import com.example.user.module.sys.service.SysUserService;
import com.example.util.common.CopyUtils;
import com.example.util.kit.SnowFlake;
import com.example.util.security.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 * @date 2019-04-01 13:06:24
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysUserRoleDao sysUserRoleDao;


    @Override
    public List<SysUserDto> queryList(Map<String,Object> params){
        List<SysUserEntity> list = sysUserDao.queryList(params);
        return CopyUtils.copyList(list, SysUserDto.class);
    }

    @Override
    public int queryTotal(Map<String, Object> params){
        return sysUserDao.queryTotal(params);
    }

    @Override
    @Transactional
    public void save(SysUserDto dto){


        dto.setId(SnowFlake.nextId());
        dto.setStatus((byte) 1);
        Date now = new Date();

        dto.setPassword(MD5.md5("123456"));

        saveUserRoleRelation(dto);


        SysUserEntity entity = CopyUtils.copyObj(dto, SysUserEntity.class);
        sysUserDao.insert(entity);
    }



    @Override
    @Transactional
    public void updateById(SysUserDto dto){

        SysUserEntity entity = CopyUtils.copyObj(dto, SysUserEntity.class);
        sysUserDao.updateById(entity);

        saveUserRoleRelation(dto);
    }

    @Override
    public SysUserDto selectById(Serializable id){
        SysUserEntity entity = sysUserDao.selectById(id);
        SysUserDto sysUserDto = CopyUtils.copyObj(entity, SysUserDto.class);
        if (sysUserDto != null) {
            //查询出没有被删除的角色
            List<Long> roleIds = sysUserRoleDao.selectRoleIdsByUserId(id);
            if (!CollectionUtils.isEmpty(roleIds)) {
                List<SysRoleDto> userRoleList = new ArrayList<>();
                for (Long roleId : roleIds) {
                    SysRoleEntity sysRoleEntity = sysRoleDao.selectById(roleId);
                    userRoleList.add(CopyUtils.copyObj(sysRoleEntity, SysRoleDto.class));
                }
                sysUserDto.setUserRoleList(userRoleList);
            }
        }
        return sysUserDto;
    }

    @Override
    @Transactional
    public void deleteById(Serializable id){
        sysUserDao.deleteById(id);
        //清除用户与角色的关系
        sysUserRoleDao.delete(new QueryWrapper<SysUserRoleEntity>().eq("user_id", id));
    }

    @Override
    @Transactional
    public void deleteBatch(Serializable[] ids){
        for (Serializable id : ids) {
            deleteById(id);
        }
    }

    @Override
    public SysUserDto selectByAccount(String account) {
        SysUserEntity entity = sysUserDao.selectOne(new QueryWrapper<SysUserEntity>().eq("account", account).last("limit 1"));
        return CopyUtils.copyObj(entity, SysUserDto.class);
    }

    @Override
    public void updatePassword(Long userId, String password) {
        SysUserEntity sysUserEntity = new SysUserEntity();
        sysUserEntity.setId(userId);
        sysUserEntity.setPassword(MD5.md5(password));
        sysUserDao.updateById(sysUserEntity);
    }

    private void saveUserRoleRelation(SysUserDto dto) {
        sysUserRoleDao.delete(new QueryWrapper<SysUserRoleEntity>().eq("user_id", dto.getId()));
        if (CollectionUtils.isEmpty(dto.getUserRoleList())) {
            return;
        }
        for (SysRoleDto sysRoleDto : dto.getUserRoleList()) {
            SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
            sysUserRoleEntity.setId(SnowFlake.nextId());
            sysUserRoleEntity.setUserId(dto.getId());
            sysUserRoleEntity.setRoleId(sysRoleDto.getId());
            sysUserRoleDao.insert(sysUserRoleEntity);
        }
    }
}
