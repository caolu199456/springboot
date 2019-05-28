package com.example.user.module.sys.service;


import com.example.user.module.sys.dto.SysUserDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 * @date 2019-04-01 13:06:24
 */
public interface SysUserService {

    /**
    * 查询列表
    */
    List<SysUserDto> queryList(Map<String, Object> params);

    /**
   * 查询数量
   */
    int queryTotal(Map<String, Object> map);

    /**
    * 存入一条记录
    * @param dto
    */
    void save(SysUserDto dto);

    /**
    * 根据id更新一条记录
    * @param dto
    */
    void updateById(SysUserDto dto);

    /**
    * 根据id查询一条记录
    * @param id
    */
    SysUserDto selectById(Serializable id);

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
     * 根据账号查询用户
     * @param account
     * @return
     */
    SysUserDto selectByAccount(String account);

    /**
     *
     * @param userId
     * @param password
     */
    void updatePassword(Long userId, String password);
}
