package com.phishguard.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")             // 모든 API 경로에 대해
                .allowedOriginPatterns("*")    // 모든 출처 허용 (보안 강화 시 특정 IP/도메인으로 변경)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*")           // 모든 헤더 허용
                .allowCredentials(true)        // 인증 정보 허용
                .maxAge(3600);                 // 설정 캐시 시간 (1시간)
    }
}
