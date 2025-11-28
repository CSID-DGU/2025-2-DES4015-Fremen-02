package com.phishguard.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("스미싱 탐지 API Server")
                        .description("안드로이드 MVP 앱을 위한 스미싱 탐지 API 명세서입니다.")
                        .version("v1.0"));
    }
}
