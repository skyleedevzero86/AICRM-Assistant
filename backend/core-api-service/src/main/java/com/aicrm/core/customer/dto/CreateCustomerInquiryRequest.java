package com.aicrm.core.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "고객 문의 등록 요청")
public record CreateCustomerInquiryRequest(
        @Schema(description = "고객 이름", example = "홍길동")
        @NotBlank String customerName,
        @Schema(description = "연락처", example = "010-1234-5678")
        @NotBlank String phone,
        @Schema(description = "이메일", example = "hong@example.com")
        @NotBlank @Email String email,
        @Schema(description = "상담 카테고리 ID", example = "5")
        @NotNull Long categoryId,
        @Schema(description = "문의 제목", example = "배송 지연 문의")
        @NotBlank String title,
        @Schema(description = "문의 내용", example = "주문한 상품이 아직 도착하지 않았습니다.")
        @NotBlank String content
) {
}
