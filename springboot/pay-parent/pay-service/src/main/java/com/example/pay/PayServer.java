package com.example.pay;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;

@SpringBootApplication
@EnableDubbo
@EnableConfigurationProperties
public class PayServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayServer.class);

    public static void main(String[] args) throws IOException {

        SpringApplication.run(PayServer.class);

        LOGGER.info("本项目可以申请最大的内存为:{}m", Runtime.getRuntime().maxMemory() / 1024 / 1024);

    }
}