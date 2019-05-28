package com.example.user.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 *@author caolu
 *@date 2019-04-03 23:10:04
 */
@Data
@TableName("sys_user_role")
public class SysUserRoleEntity {

    /**
    * 
    */
    @TableId
    private Long id;
    /**
    * 
    */
    private Long userId;
    /**
    * 
    */
    private Long roleId;

}
