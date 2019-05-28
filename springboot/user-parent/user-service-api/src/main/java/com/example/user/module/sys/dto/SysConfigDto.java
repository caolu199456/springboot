package com.example.user.module.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *属性配置表
 *@author caolu
 *@date 2019-04-18 09:43:48
 */
@Data
public class SysConfigDto implements Serializable {

    /**
    * 
    */
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
