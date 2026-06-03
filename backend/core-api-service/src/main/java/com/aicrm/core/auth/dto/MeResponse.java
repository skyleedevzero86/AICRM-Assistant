package com.aicrm.core.auth.dto;

import com.aicrm.core.auth.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 정보 응답")
public record MeResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        @Schema(description = "이메일", example = "customer@example.com")
        String email,
        @Schema(description = "이름", example = "홍길동")
        String name,
        @Schema(description = "역할", example = "CUSTOMER")
        UserRole role,
        @Schema(description = "전화번호", example = "010-1234-5678")
        String phone,
        @Schema(description = "사번 (상담원)", example = "1234567890123456")
        String employeeNo
) {
}
