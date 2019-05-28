package com.example.cms.interceptor;

import lombok.Data;

import java.util.Set;

/**
 * 在这里编写说明
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-03-01 9:15:00
 */
@Data
public class LoginUserSession {

    private String token;

    private Long userId;
    /**
     * 用户名
     */
    private String username;

    /**
     * 账号
     */
    private String account;
    /**
     * 用户的角色
     */
    private Set<String> userRoles;
    /**
     * 用户的权限
     */
    private Set<String> permissions;
}
