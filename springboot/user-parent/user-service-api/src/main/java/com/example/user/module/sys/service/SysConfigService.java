package com.example.user.module.sys.service;


import com.example.user.module.sys.dto.SysConfigDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 * @date 2019-04-18 09:43:48
 */
public interface SysConfigService {

    /**
    * 查询列表
    */
    List<SysConfigDto> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> map);

    /**
    * 存入一条记录
    * @param dto
    */
    void save(SysConfigDto dto);

    /**
    * 根据id更新一条记录
    * @param dto
    */
    void updateById(SysConfigDto dto);

    /**
    * 根据id查询一条记录
    * @param id
    */
    SysConfigDto selectById(Serializable id);

    /**
    * 根据id删除一条记录
    * @param id
    */
    void deleteById(Serializable id);

    /**
   * 批量删除
   * @param ids
   */
    void deleteBatch(Serializable[] ids);

    /**
     * 根据key配置的参数值 本方法自带缓存直接调用即可
     * @param configKey
     * @return
     */
    String getConfigValueByKey(String configKey);
}
