package com.example.cms.controller;

import com.example.cms.constants.SystemConstants;
import com.example.cms.interceptor.LoginUserSession;
import com.example.util.common.ReflectUtils;
import com.example.util.common.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    @Autowired
    protected HttpServletRequest httpServletRequest;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleException(Exception e) {
        e.printStackTrace();
        return Response.wrap(500,"服务器好像出了点小问题");
    }

    protected LoginUserSession getLoginUser() {
        String token = httpServletRequest.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            token = httpServletRequest.getParameter("token");
        }
        if (StringUtils.isNotBlank(token)) {
            //header和url都没有传入token
            return (LoginUserSession) httpServletRequest.getAttribute(SystemConstants.USER_SESSION_SAVE_KEY + token);
        }
        return null;
    }

    /**
     * 获取当前登陆用户Id
     *
     * @return
     */
    protected Long getUserId() {
        LoginUserSession loginUser = getLoginUser();
        return loginUser.getUserId();
    }

    /**
     * 填充保存时的公共参数
     * @param obj
     */
    protected void fillSaveCommonData(Object obj) {
        LoginUserSession loginUser = getLoginUser();
        if (obj != null && loginUser != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("creator", loginUser.getAccount());
            params.put("createTime", new Date());
            ReflectUtils.setFields(obj, params);
        }
    }
    /**
     * 填充编辑时的公共参数
     * @param obj
     */
    protected void fillEditCommonData(Object obj) {
        LoginUserSession loginUser = getLoginUser();
        if (obj != null && loginUser != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("editor", loginUser.getAccount());
            params.put("editTime", new Date());
            ReflectUtils.setFields(obj, params);
        }
    }
}
