package com.example.user.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 *@author caolu
 *@date 2019-04-01 13:06:24
 */
@Data
@TableName("sys_user")
public class SysUserEntity {

    /**
    * 
    */
    @TableId
    private Long id;
    /**
    * 
    */
    private String account;
    /**
    * 
    */
    private String username;
    /**
    * 
    */
    private String password;
    /**
    * 0锁定1正常
    */
    private Byte status;

    private String creator;

    private Date createTime;

    private Date editor;

    private Date editTime;
}
