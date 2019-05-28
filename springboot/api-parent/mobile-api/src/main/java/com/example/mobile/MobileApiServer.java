package com.example.mobile;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@EnableDubbo
public class MobileApiServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileApiServer.class);

    public static void main(String[] args) throws IOException {

        SpringApplication.run(MobileApiServer.class);

        LOGGER.info("本项目可以申请最大的内存为:{}m", Runtime.getRuntime().maxMemory() / 1024 / 1024);
    }


}