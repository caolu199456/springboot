package com.example.user.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *属性配置表
 *@author caolu
 *@date 2019-04-18 09:43:46
 */
@Data
@TableName("sys_config")
public class SysConfigEntity {

    /**
    * 
    */
    @TableId
    private Long id;
    /**
    * 
    */
    private String configKey;
    /**
    * 
    */
    private String configValue;
    /**
    * 
    */
    private String remark;
    /**
    * 
    */
    private String creator;
    /**
    * 
    */
    private Date createTime;
    /**
    * 
    */
    private String editor;
    /**
    * 
    */
    private Date editTime;

}
