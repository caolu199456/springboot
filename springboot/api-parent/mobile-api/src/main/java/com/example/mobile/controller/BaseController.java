package com.example.mobile.controller;

import com.example.mobile.constants.SystemConstants;
import com.example.mobile.vo.h5.member.H5MemberInfoVo;
import com.example.util.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Autowired
    protected HttpServletRequest request;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleException(Exception e) {
        return Response.wrap(500,"服务器好像出了点小问题");
    }

    /**
     * 校验H5是否已经经过授权
     * @return
     */
    protected H5MemberInfoVo getH5MemberInfo() {
        H5MemberInfoVo h5MemberInfoVo = (H5MemberInfoVo) request.getAttribute(SystemConstants.JS_MEMBER_INFO_REQUEST_SCOPE_KEY);
        return h5MemberInfoVo;
    }
}
