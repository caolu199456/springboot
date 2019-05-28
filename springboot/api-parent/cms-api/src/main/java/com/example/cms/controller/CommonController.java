package com.example.cms.controller;

import com.example.cms.constants.RedisCacheConstants;
import com.example.util.common.*;
import com.example.util.redis.RedisUtils;
import com.example.util.security.UUIDUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("common")
public class CommonController extends BaseController{
    /**
     * 创建二维码
     */
    @GetMapping("createQRCode")
    public void createQRCode(@RequestParam String content, HttpServletResponse response ){
        try {
            QRCodeUtils.create(content, 300, 300, "png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 创建验证码
     */
    @GetMapping("createCaptcha")
    public Response createCaptcha() throws IOException {
        String captchaId = UUIDUtils.gen32UUID();
        String captchaCode = RandomUtils.genCharsCode(4);

        RedisUtils.set(RedisCacheConstants.CAPTCHA_ID + captchaId, captchaCode, 2, TimeUnit.MINUTES);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CaptchaUtils.outputImage(300,100, baos, captchaCode);

        Map<String, String> resp = new HashMap<>();
        resp.put("captchaId", captchaId);
        resp.put("imgBase64", ImageUtils.base64Encode(baos));

        return Response.ok().wrap(resp);
    }
}
