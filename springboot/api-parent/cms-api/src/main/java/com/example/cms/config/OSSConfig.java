package com.example.cms.config;

import com.aliyun.oss.OSSClient;
import com.example.util.http.OSSUtils;
import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oss")
@Data
public class OSSConfig {
    private String key;

    private String secret;

    private String endpoint;

    private String defaultBucket;
    @Bean
    public OSSUtils getOssUtils(){
        CuratorFramework curatorFramework = null;
        return new OSSUtils(new OSSClient(endpoint, key, secret), defaultBucket);
    }
}
