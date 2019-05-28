package com.example.user.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 *@author caolu
 *@date 2019-04-02 09:42:45
 */
@Data
@TableName("sys_menu")
public class SysMenuEntity {

    /**
    * 
    */
    @TableId
    private Long id;
    /**
    * 
    */
    private Long parentId;
    /**
    * 
    */
    private String name;
    /**
    * 
    */
    private String icon;
    /**
    * url
    */
    private String url;
    /**
    * 菜单等级1顶级2子菜单3按钮
    */
    private Byte level;
    /**
    * 权限名称 如sysUser:edit
    */
    private String permission;
    /**
    * 0禁用1正常
    */
    private Byte status;
    /**
    * 排序每一个菜单下都是从0开始
    */
    private Integer sort;

    private String creator;

    private Date createTime;

    private Date editor;

    private Date editTime;


}
