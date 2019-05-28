package com.example.mobile.controller.h5.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.mobile.controller.BaseController;
import com.example.pay.module.common.dto.JsUserInfo;
import com.example.pay.module.common.service.CommonService;
import com.example.util.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息获取
 */
@RestController
@RequestMapping("h5/h5User")
@Api("h5用户相关")
public class H5UserController extends BaseController {
    @Reference
    private CommonService commonService;

    @GetMapping("getWxUserInfo")
    @ApiOperation("根据authCode获取微信用户信息")
    public Response<JsUserInfo> getWxUserInfo(@RequestParam String authCode) {
        return Response.ok().wrap(commonService.getWxUserInfo(authCode));
    }

    @GetMapping("getZfbUserInfo")
    @ApiOperation("根据authCode获取支付宝用户信息")
    public Response<JsUserInfo> getZfbUserInfo(@RequestParam String authCode) {
        return Response.ok().wrap(commonService.getZfbUserInfo(authCode));
    }

}
