package com.example.message;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.embedded.TomcatWebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
@EnableDubbo
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) throws IOException {

        SpringApplication.run(Application.class);

        LOGGER.info("本项目可以申请最大的内存为:{}m",Runtime.getRuntime().maxMemory()/1024/1024);
    }

}