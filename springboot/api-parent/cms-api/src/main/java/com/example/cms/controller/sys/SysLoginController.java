package com.example.cms.controller.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.example.cms.constants.RedisCacheConstants;
import com.example.cms.controller.BaseController;
import com.example.cms.interceptor.LoginUserSession;
import com.example.user.module.sys.dto.SysMenuDto;
import com.example.user.module.sys.dto.SysRoleDto;
import com.example.user.module.sys.dto.SysUserDto;
import com.example.user.module.sys.service.SysRoleService;
import com.example.user.module.sys.service.SysUserService;
import com.example.util.common.Response;
import com.example.util.redis.RedisUtils;
import com.example.util.security.MD5;
import com.example.util.security.RSAUtils;
import com.example.util.security.TokenUtils;
import com.example.util.security.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 登录说明
 * 1 获取公钥和登录ID
 * 2 传入公钥加密后的密码和登录ID
 * 3 根据登录ID获取redis中的私钥进行解密
 * 4 正常的登录流程
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-04-04 11:07:00
 */
@RestController
@RequestMapping("sys")
public class SysLoginController extends BaseController {
    @Reference
    private SysUserService sysUserService;
    @Reference
    private SysRoleService sysRoleService;


    /**
     * 前端获取公钥 然后把加密后的密码传入过来
     */
    @GetMapping("getPublicKey")
    public Response getPublicKey() {
        try {
            Map<String, Object> keyPair = RSAUtils.genKeyPair();
            String privateKey = RSAUtils.getPrivateKey(keyPair);
            String publicKey = RSAUtils.getPublicKey(keyPair);
            String loginId = UUIDUtils.gen32UUID();
            RedisUtils.set(RedisCacheConstants.LOGIN_ID + loginId, privateKey, 5, TimeUnit.MINUTES);

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("loginId", loginId);
            dataMap.put("publicKey", publicKey);
            return Response.ok().wrap(dataMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.error("获取公钥失败");
    }

    /**
     * 保存
     */
    @PostMapping("login")
    public Response login(@RequestBody SysUserDto recData) {
        String account = recData.getAccount();
        String encryptPassword = recData.getEncryptPassword();
        String loginId = recData.getLoginId();
        if (StringUtils.isEmpty(account)
                ||
                StringUtils.isEmpty(loginId)
                ||
                StringUtils.isEmpty(encryptPassword)

        ) {
            return Response.error("参数不完整");
        }

        String privateKey = RedisUtils.get(RedisCacheConstants.LOGIN_ID + loginId);
        if (StringUtils.isEmpty(privateKey)) {
            //因为loginId 5分钟过期
            return Response.error("页面已过期请刷新后再试");
        }
        String password = null;
        try {
            password = new String(RSAUtils.decryptByPrivateKey(Base64.getDecoder().decode(encryptPassword), privateKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SysUserDto sysUserDto = sysUserService.selectByAccount(account);

        if (sysUserDto == null || !sysUserDto.getPassword().equals(MD5.md5(password))) {
            return Response.error("账号或者密码错误");
        }
        if (Objects.equals(sysUserDto.getStatus(), new Byte("0"))) {
            return Response.error("账号禁用");
        }

        //私钥已经没有用了
        RedisUtils.del(RedisCacheConstants.LOGIN_ID + loginId);

        Set<String> userRoles = new HashSet<>();
        Set<String> userPermissions = new HashSet<>();

        List<SysRoleDto> userRoleList = sysUserService.selectById(sysUserDto.getId()).getUserRoleList();
        for (SysRoleDto sysRoleDto : userRoleList) {
            if (StringUtils.isNotBlank(sysRoleDto.getRoleName())) {
                userRoles.add(sysRoleDto.getRoleName());
            }
            List<SysMenuDto> userMenuList = sysRoleService.selectById(sysRoleDto.getId()).getRoleMenuList();
            for (SysMenuDto sysMenuDto : userMenuList) {
                if (sysMenuDto.isChecked() && StringUtils.isNotBlank(sysMenuDto.getPermission())) {
                    userPermissions.add(sysMenuDto.getPermission());
                }
            }
        }
        String token = TokenUtils.createToken(null, null);

        LoginUserSession session = new LoginUserSession();
        session.setToken(token);
        session.setUserId(sysUserDto.getId());
        session.setUsername(sysUserDto.getUsername());
        session.setAccount(sysUserDto.getAccount());
        session.setUserRoles(userRoles);
        session.setPermissions(userPermissions);

        RedisUtils.set(RedisCacheConstants.TOKEN_PREFIX + session.getToken(), JSON.toJSONString(session), 30, TimeUnit.MINUTES);

        return Response.ok().wrap(JSON.toJSONString(session));
    }

    /**
     * 获取用户菜单
     */
    @GetMapping("getMenuList")
    public Response getMenuList() {
        List<SysMenuDto> result = new ArrayList<>();
        Long userId = getUserId();

        List<SysRoleDto> userRoleList = sysUserService.selectById(userId).getUserRoleList();
        for (SysRoleDto sysRoleDto : userRoleList) {
            List<SysMenuDto> userMenuList = sysRoleService.selectById(sysRoleDto.getId()).getRoleMenuList();
            for (SysMenuDto sysMenuDto : userMenuList) {
                if (sysMenuDto.isChecked()) {
                    //得到用户角色所有的菜单
                    if (!result.contains(sysMenuDto)) {
                        //去重 已经重写equals
                        result.add(sysMenuDto);
                    }
                }
            }
        }
        return Response.ok().wrap(result);
    }

    /**
     * 获取用户菜单
     */
    @PostMapping("updatePassword")
    public Response updatePassword(@RequestBody HashMap param) {
        LoginUserSession loginUser = getLoginUser();
        if (loginUser != null) {
            sysUserService.updatePassword(loginUser.getUserId(), String.valueOf(param.get("password") + ""));
        }
        return Response.ok();

    }

    /**
     * 获取用户菜单
     */
    @PostMapping("logout")
    public Response logout() {
        LoginUserSession loginUser = getLoginUser();
        if (loginUser != null) {

            RedisUtils.del(RedisCacheConstants.TOKEN_PREFIX + loginUser.getToken());
        }
        return Response.ok();

    }
}
