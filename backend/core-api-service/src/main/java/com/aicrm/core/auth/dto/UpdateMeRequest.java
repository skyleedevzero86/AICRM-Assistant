package com.aicrm.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "내 정보 수정 요청")
public record UpdateMeRequest(
        @Schema(description = "이름", example = "홍길동")
        @Size(max = 100) String name,
        @Schema(description = "새 비밀번호 (8자 이상)", example = "newpassword1234")
        @Size(min = 8, max = 100) String password,
        @Schema(description = "전화번호", example = "010-9876-5432")
        @Size(max = 30) String phone
) {
}
