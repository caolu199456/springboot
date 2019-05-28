package com.example.user.module.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 *@author caolu
 *@date 2019-04-01 13:06:24
 */
@Data
public class SysUserDto implements Serializable {

    /**
    * 
    */
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
     * 公钥
     */
    private String loginId;

    private String encryptPassword;
    /**
    * 0锁定1正常
    */
    private Byte status;

    private String creator;

    private Date createTime;

    private Date editor;

    private Date editTime;

    private List<SysRoleDto> userRoleList = new ArrayList<>();

}
