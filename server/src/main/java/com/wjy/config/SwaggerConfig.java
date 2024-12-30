package com.wjy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("管理端接口")
                .pathsToMatch("/admin/**") // 根据你的实际路径进行配置
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("客户端接口")
                .pathsToMatch("/user/**") // 根据你的实际路径进行配置
                .build();
    }

    @Bean
    public OpenAPI publicAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("接口文档")
                        //描叙
                        .description("接口文档")
                        //版本
                        .version("v1")
                        //作者信息，自行设置
                        .contact(new Contact().name("Jeremisty"))
                        //设置接口文档的许可证信息
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
