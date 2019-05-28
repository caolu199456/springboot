package com.example.mobile.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.mobile.constants.RedisCacheConstants;
import com.example.mobile.constants.SystemConstants;
import com.example.mobile.constants.SystemResp;
import com.example.mobile.vo.h5.member.H5MemberInfoVo;
import com.example.util.common.Response;
import com.example.util.redis.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
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
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith(request.getContextPath() + "/h5")) {
            //h5相关接口拦截器
            String token = request.getHeader("token");
            if (StringUtils.isBlank(token)) {
                token = request.getParameter("token");
            }
            if (StringUtils.isBlank(token)) {
                writeError(response, Response.error("必须传入Token"));
                return false;
            }
            String h5MemberInfoStr = RedisUtils.get(RedisCacheConstants.H5_TOKEN + token);
            if (StringUtils.isBlank(h5MemberInfoStr)) {
                writeError(response, Response.wrap(SystemResp.TOKEN_EXPIRED_CODE, SystemResp.TOKEN_EXPIRED_MSG));
                return false;
            }
            //重新设置时间
            RedisUtils.expire(SystemConstants.JS_MEMBER_INFO_REQUEST_SCOPE_KEY + token, 30, TimeUnit.MINUTES);
            //方便controller获取
            request.setAttribute(SystemConstants.JS_MEMBER_INFO_REQUEST_SCOPE_KEY, JSON.parseObject(h5MemberInfoStr, H5MemberInfoVo.class));

            return true;
        }
        return true;
    }
    private void writeError(HttpServletResponse response, Response result) {
        try {
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
                .excludePathPatterns("/h5/member/getWxUserInfo")
                .excludePathPatterns("/h5/member/getZfbUserInfo")
                .excludePathPatterns("/h5/orderH5Pay/wxH5RefundNotify")
                .excludePathPatterns("/h5/orderH5Pay/h5PayNotify/*");
    }
}
