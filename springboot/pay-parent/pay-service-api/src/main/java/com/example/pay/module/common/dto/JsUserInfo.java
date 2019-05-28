package com.example.pay.module.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class JsUserInfo implements Serializable {
    /**
     * 用户id
     */
    private String buyerId;
    /**
     * 1代表支付宝
     * 2代表微信
     */
    private Byte userType;
}
