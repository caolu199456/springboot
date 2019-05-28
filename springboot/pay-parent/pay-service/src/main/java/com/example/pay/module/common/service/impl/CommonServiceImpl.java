package com.exmaple.pay.module.common.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.example.pay.module.common.dto.JsUserInfo;
import com.example.pay.module.common.service.CommonService;
import com.example.pay.sdk.wx.WXPayConfig;
import com.example.pay.sdk.wx.WXPayUtil;
import com.example.util.http.HttpRestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommonServiceImpl implements CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);

    @Autowired
    private AlipayClient alipayClient;

    @Resource(name = "wxH5Pay")
    private WXPayConfig wxH5PayConfig;
    @Resource(name = "wxAppPay")
    private WXPayConfig wxAppPayConfig;

    private static final HttpRestUtils HTTP_REST_UTILS = HttpRestUtils.getDefaultInstance();

    @Override
    public Map<String, String> getWxJSSDKConfig() {
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("appId", wxH5PayConfig.getAppID());
        configMap.put("timestamp", WXPayUtil.getCurrentTimestamp() + "");
        configMap.put("nonceStr", WXPayUtil.generateNonceStr());
        try {
            String signature = WXPayUtil.generateSignature(configMap, wxH5PayConfig.getKey());
            configMap.put("signature", signature);
            return configMap;
        } catch (Exception e) {
            throw new RuntimeException("获取微信JS-SDK配置出错");
        }
    }

    @Override
    public JsUserInfo getWxUserInfo(String authCode) {
        String requestUrl = String.format(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                wxH5PayConfig.getAppID(),
                wxH5PayConfig.getSecret(),
                authCode
        );
        String responseStr = (String) HTTP_REST_UTILS.get(requestUrl, null, null, String.class).getRight();
        if (StringUtils.isNoneBlank(responseStr)) {
            JSONObject recData = JSON.parseObject(responseStr);

            JsUserInfo jsUserInfo = new JsUserInfo();
            jsUserInfo.setBuyerId(recData.getString("openid"));
            jsUserInfo.setUserType((byte) 2);
            return jsUserInfo;
        }
        return null;
    }

    @Override
    public JsUserInfo getZfbUserInfo(String authCode) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authCode);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            JsUserInfo jsUserInfo = new JsUserInfo();
            jsUserInfo.setBuyerId(oauthTokenResponse.getUserId());
            jsUserInfo.setUserType((byte) 1);
            return jsUserInfo;
        } catch (AlipayApiException e) {
            //处理异常
            LOGGER.error("获取支付宝用户信息数据失败", e);
        }
        return null;
    }


}
