package com.example.message.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by CL on 2017/8/8.
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
    @Value("${swagger.isOpen}")
    private boolean isOpen;

    /**
     * 地址 http://localhost:8080/swagger-ui.html
     * @return
     */
    @Bean
    public Docket createRestApi() {
        if (!isOpen) {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.example"))
                    .paths(PathSelectors.none())
                    .build();
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
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
