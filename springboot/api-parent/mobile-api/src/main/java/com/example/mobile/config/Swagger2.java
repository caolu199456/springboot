package com.example.mobile.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Objects;

/**
 * Created by CL on 2017/8/8.
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * 地址 http://localhost:8080/swagger-ui.html
     * @return
     */
    @Bean
    public Docket createRestApi() {
        boolean isOpen = false;
        if (Objects.equals(applicationContext.getEnvironment().getActiveProfiles()[0], "dev")) {
            isOpen = true;
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(isOpen)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example"))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
                .description("")
                .termsOfServiceUrl("")
                .contact("")
                .version("1.0")
                .build();
    }

}
