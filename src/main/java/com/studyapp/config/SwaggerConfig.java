package com.studyapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger配置
 * Swagger Configuration
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.studyapp.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("考研学习小程序API文档")
                .description("基于SpringBoot的考研学习小程序接口文档")
                .version("1.0.0")
                .contact(new Contact("开发团队", "", ""))
                .build();
    }
}
