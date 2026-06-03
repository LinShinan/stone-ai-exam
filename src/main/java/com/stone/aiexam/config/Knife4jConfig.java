package com.stone.aiexam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置类
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stone AI Exam API文档")
                        .version("1.0.0")
                        .description("AI考试系统接口文档")
                        .contact(new Contact()
                                .name("Stone AI Team")
                                .email("support@stone-ai.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("stone-ai-exam")
//                .pathsToMatch("/**")
//                .build();
//    }


    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("公共端")                              // ← 下拉框显示的名字
                .pathsToMatch("/api/common/**")               // ← 匹配这个路径的
                .build();
    }

    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("用户端")
                .pathsToMatch("/api/student/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("管理端")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("认证")
                .pathsToMatch("/api/auth/**")
                .build();
    }

}