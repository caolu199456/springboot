package com.example.mobile.config;

import com.example.util.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;

@Configuration
public class RedisConfig {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        RedisUtils.config(redisTemplate);
    }

}
