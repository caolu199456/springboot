package com.example.pay.module.common.service;

import com.example.pay.module.common.dto.JsUserInfo;

import java.util.Map;

/**
 * 这个包写一些公共方法 比如获取微信openId 支付宝userId
 */
public interface CommonService {

    /**
     * 得到微信JS-SDK初始化配置信息 用户调起摄像头 录音 图像等
     * wx.config({
     *     debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
     *     appId: '', // 必填，公众号的唯一标识
     *     timestamp: , // 必填，生成签名的时间戳
     *     nonceStr: '', // 必填，生成签名的随机串
     *     signature: '',// 必填，签名
     *     jsApiList: [] // 必填，需要使用的JS接口列表
     * });
     * 我们这里不返回 debug jsApiList 由前端控制
     * @return
     */
    Map<String, String> getWxJSSDKConfig();
    /**
     * 获取微信用户的信息
     * @param authCode 前端传入
     * @return
     */
    JsUserInfo getWxUserInfo(String authCode);

    /**
     * 获取微信用户的信息
     * @param authCode 前端传入
     * @return
     */
    JsUserInfo getZfbUserInfo(String authCode);
}
