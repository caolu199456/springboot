package com.example.cms.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.cms.constants.RedisCacheConstants;
import com.example.cms.constants.SystemConstants;
import com.example.util.annotation.RequirePermissions;
import com.example.util.annotation.RequireRoles;
import com.example.util.common.Response;
import com.example.util.redis.RedisUtils;
import com.example.util.spring.SpringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 在这里编写说明
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-02-28 17:37:00
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter implements WebMvcConfigurer {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            //请求的不是方法
            return true;
        }
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        if (StringUtils.isEmpty(token)) {
            //header和url都没有传入token
            writeError(response, Response.wrap(20001, "请登录"));
            return false;
        }
        //验证token是否过期
        String loginUserInfoStr = RedisUtils.get(RedisCacheConstants.TOKEN_PREFIX + token);
        if (StringUtils.isEmpty(loginUserInfoStr)) {
            writeError(response, Response.wrap(20002, "登录状态已过期,请重新的登录"));
            return false;
        }

        LoginUserSession loginUserInfo = JSON.parseObject(loginUserInfoStr, LoginUserSession.class);

        HandlerMethod method = (HandlerMethod) handler;
        //验证是否拥有该权限
        RequirePermissions requirePermissions = method.getMethodAnnotation(RequirePermissions.class);
        if (requirePermissions != null) {
            for (String value : requirePermissions.values()) {
                if (!loginUserInfo.getPermissions().contains(value)) {
                    writeError(response, Response.wrap(20003, "权限不足"));
                    return false;
                }
            }
        }

        //是否有该角色
        RequireRoles requireRoles = method.getMethodAnnotation(RequireRoles.class);
        if (requireRoles != null) {
            for (String value : requireRoles.values()) {
                if (!loginUserInfo.getUserRoles().contains(value)) {
                    writeError(response, Response.wrap(20003, "权限不足"));
                    return false;
                }
            }
        }
        RedisUtils.expire(RedisCacheConstants.TOKEN_PREFIX + token,30, TimeUnit.MINUTES);
        request.setAttribute(SystemConstants.USER_SESSION_SAVE_KEY + token, loginUserInfo);
        return true;
    }


    private void writeError(HttpServletResponse response, Response result) {
        try {
            response.setHeader("Content-Type", "application/json");
            ServletOutputStream out = response.getOutputStream();
            out.write(JSON.toJSONString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this)
                .addPathPatterns("/**")
                .excludePathPatterns("/welcome")
                .excludePathPatterns("/common/*")
                .excludePathPatterns("/sys/login")
                .excludePathPatterns("/sys/getPublicKey");
    }
}
