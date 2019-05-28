package com.example.user.module.sys.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.user.module.sys.dao.SysConfigDao;
import com.example.user.module.sys.dto.SysConfigDto;
import com.example.user.module.sys.entity.SysConfigEntity;
import com.example.user.module.sys.service.SysConfigService;
import com.example.util.common.CopyUtils;
import com.example.util.kit.SnowFlake;
import com.example.util.redis.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author caolu
 * @date 2019-04-18 09:43:48
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysConfigService.class);

    @Autowired
    private SysConfigDao sysConfigDao;

    private static final String SYS_CONFIG_CACHE_KEY = "SYS_CONFIG_CACHE_KEY";
    @Override
    public List<SysConfigDto> queryList(Map<String,Object> params){
        List<SysConfigEntity> list = sysConfigDao.queryList(params);
        return CopyUtils.copyList(list, SysConfigDto.class);
    }

    @Override
    public int queryTotal(Map<String, Object> params){
        return sysConfigDao.queryTotal(params);
    }

    @Override
    public void save(SysConfigDto dto){
        SysConfigEntity entity = CopyUtils.copyObj(dto,SysConfigEntity.class);
        entity.setId(SnowFlake.nextId());
        sysConfigDao.insert(entity);

        //缓存key
        RedisUtils.set(SYS_CONFIG_CACHE_KEY + dto.getConfigKey(), dto.getConfigValue(), 15, TimeUnit.MINUTES);
    }

    @Override
    public void updateById(SysConfigDto dto){
        SysConfigEntity entity = CopyUtils.copyObj(dto,SysConfigEntity.class);
        sysConfigDao.updateById(entity);

        //缓存key
        RedisUtils.set(SYS_CONFIG_CACHE_KEY + dto.getConfigKey(), dto.getConfigValue(), 15, TimeUnit.MINUTES);
    }

    @Override
    public SysConfigDto selectById(Serializable id){
        SysConfigEntity entity = sysConfigDao.selectById(id);
        return CopyUtils.copyObj(entity, SysConfigDto.class);
    }

    @Override
    public void deleteById(Serializable id){
        SysConfigDto sysConfigDto = selectById(id);
        if (sysConfigDto != null) {
            //删除缓存的key
            RedisUtils.del(SYS_CONFIG_CACHE_KEY + sysConfigDto.getConfigKey());

        }
        sysConfigDao.deleteById(id);

    }

    @Override
    @Transactional
    public void deleteBatch(Serializable[] ids){
        for (Serializable id : ids) {
            deleteById(id);
        }
    }

    @Override
    public String getConfigValueByKey(String configKey) {

        String value = RedisUtils.get(SYS_CONFIG_CACHE_KEY + configKey);
        if (StringUtils.isNotBlank(value)) {
            if ("NULL_VALUE".equals(value)) {
                return null;
            }else {
                return value;
            }

        }
        SysConfigEntity configEntity = sysConfigDao.selectOne(new QueryWrapper<SysConfigEntity>().eq("config_key", configKey).last("limit 1"));
        if (configEntity != null) {
            RedisUtils.set(SYS_CONFIG_CACHE_KEY + configEntity.getConfigKey(), configEntity.getConfigValue(), 15, TimeUnit.MINUTES);
        }else {
            RedisUtils.set(SYS_CONFIG_CACHE_KEY + configEntity.getConfigKey(), "NULL_VALUE", 15, TimeUnit.MINUTES);
        }

        return configEntity == null ? null : configEntity.getConfigValue();
    }
}
