package com.aicrm.core.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OpenApiConfig.class)
class OpenApiConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void jwtBearerSecuritySchemeIsRegistered() {
        // given
        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get(OpenApiConfig.BEARER_AUTH);

        // when & then
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
        assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
        assertThat(scheme.getDescription()).contains("JWT");
    }

    @Test
    void apiInfoIsConfiguredInKorean() {
        // given
        var info = openAPI.getInfo();

        // when & then
        assertThat(info.getTitle()).isEqualTo("AICRM Core API");
        assertThat(info.getDescription()).contains("JWT");
        assertThat(info.getDescription()).contains("Authorize");
    }
}
