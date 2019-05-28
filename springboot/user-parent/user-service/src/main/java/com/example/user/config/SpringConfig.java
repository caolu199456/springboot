package com.example.user.config;

import com.example.util.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SpringConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        SpringUtils.applicationContext = this.applicationContext;
    }

}
