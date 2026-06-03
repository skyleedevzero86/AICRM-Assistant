package com.aicrm.core.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "고객 문의 수정 요청")
public record UpdateCustomerInquiryRequest(
        @Schema(description = "고객 이름", example = "홍길동")
        @NotBlank String customerName,
        @Schema(description = "이메일", example = "hong@example.com")
        @NotBlank @Email String email,
        @Schema(description = "상담 카테고리 ID", example = "5")
        @NotNull Long categoryId,
        @Schema(description = "문의 제목", example = "배송 지연 문의 (수정)")
        @NotBlank String title,
        @Schema(description = "문의 내용", example = "주문 번호 12345 배송이 지연되고 있습니다.")
        @NotBlank String content
) {
}
