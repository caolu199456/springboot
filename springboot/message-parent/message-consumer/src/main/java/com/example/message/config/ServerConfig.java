package com.example.message.config;

import com.example.util.http.NetUtils;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    /**
     * 从10000开始获取端口
     * @return
     */
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(NetUtils.getAvailablePort(10000));
    }
}
