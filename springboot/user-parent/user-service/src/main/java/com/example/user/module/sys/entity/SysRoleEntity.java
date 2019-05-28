package com.example.user.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 *@author caolu
 *@date 2019-04-01 22:52:09
 */
@Data
@TableName("sys_role")
public class SysRoleEntity {

    /**
    * ID
    */
    @TableId
    private Long id;
    /**
    * 角色中文名
    */
    private String roleCnName;
    /**
    * 角色英文名
    */
    private String roleName;
    /**
    * 状态1正常0锁定
    */
    private Byte status;

    private String creator;

    private Date createTime;

    private Date editor;

    private Date editTime;

}
