package com.aicrm.core.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AICRM Core API")
                        .description("""
                                AICRM 콜센터 업무 시스템 Core API 문서입니다.
                                인증이 필요한 API는 Swagger UI 상단 **Authorize** 버튼에서 \
                                `Bearer {JWT}` 형식으로 토큰을 입력하세요.
                                로그인(`POST /api/auth/login`) 응답의 `accessToken` 값을 사용합니다.
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("AICRM-Assistant")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        JWT 인증 헤더입니다.
                                        형식: `Authorization: Bearer {accessToken}`
                                        로그인 API에서 발급받은 accessToken을 입력하세요.
                                        """)));
    }
}
