package com.aicrm.core.category.controller;

import com.aicrm.core.category.application.GetConsultationCategoryTreeService;
import com.aicrm.core.category.dto.ConsultationCategoryTreeResponse;
import com.aicrm.core.global.response.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories/consultation")
public class ConsultationCategoryController {

    private final GetConsultationCategoryTreeService getConsultationCategoryTreeService;

    public ConsultationCategoryController(GetConsultationCategoryTreeService getConsultationCategoryTreeService) {
        this.getConsultationCategoryTreeService = getConsultationCategoryTreeService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<ConsultationCategoryTreeResponse>> getTree() {
        return ApiResponse.ok(getConsultationCategoryTreeService.getActiveTree());
    }
}
