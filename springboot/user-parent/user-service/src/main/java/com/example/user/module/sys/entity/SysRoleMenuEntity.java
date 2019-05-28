package com.example.user.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 *@author caolu
 *@date 2019-04-03 23:11:19
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenuEntity {

    /**
    * 
    */
    @TableId
    private Long id;
    /**
    * 
    */
    private Long roleId;
    /**
    * 
    */
    private Long menuId;

}
