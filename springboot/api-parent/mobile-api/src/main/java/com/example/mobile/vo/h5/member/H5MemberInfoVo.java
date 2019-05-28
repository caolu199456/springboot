package com.example.mobile.vo.h5.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class H5MemberInfoVo {
    /**
     * userid或者openid
     */
    @ApiModelProperty("授权成功后的token")
    private String token;
    /**
     * userid或者openid
     */
    @ApiModelProperty("授权后的userId")
    private String buyerId;
    /**
     * 手机号 绑定才有
     */
    @ApiModelProperty("手机号 绑定才有")
    private String mobile;
    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickName;
}
