package com.aicrm.core.category.dto;

import java.util.List;

public record ConsultationCategoryTreeResponse(
        Long id,
        String code,
        String name,
        int depth,
        List<ConsultationCategoryTreeResponse> children
) {
}
