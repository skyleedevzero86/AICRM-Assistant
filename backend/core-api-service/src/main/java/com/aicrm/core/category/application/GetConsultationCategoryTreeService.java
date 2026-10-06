package com.aicrm.core.category.application;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.category.domain.ConsultationCategoryRepository;
import com.aicrm.core.category.dto.ConsultationCategoryTreeResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetConsultationCategoryTreeService {

    private static final short ROOT_DEPTH = 1;

    private final ConsultationCategoryRepository consultationCategoryRepository;

    public GetConsultationCategoryTreeService(ConsultationCategoryRepository consultationCategoryRepository) {
        this.consultationCategoryRepository = consultationCategoryRepository;
    }

    public List<ConsultationCategoryTreeResponse> getActiveTree() {
        List<ConsultationCategory> categories = consultationCategoryRepository.findAllActiveOrdered();
        Map<Long, List<ConsultationCategory>> childrenByParentId = new LinkedHashMap<>();

        for (ConsultationCategory category : categories) {
            if (category.getParent() == null) {
                continue;
            }
            childrenByParentId
                    .computeIfAbsent(category.getParent().getId(), ignored -> new ArrayList<>())
                    .add(category);
        }

        return categories.stream()
                .filter(category -> category.getDepth() == ROOT_DEPTH)
                .sorted(Comparator.comparingInt(ConsultationCategory::getSortOrder).thenComparing(ConsultationCategory::getId))
                .map(category -> toTreeResponse(category, childrenByParentId))
                .toList();
    }

    private ConsultationCategoryTreeResponse toTreeResponse(
            ConsultationCategory category,
            Map<Long, List<ConsultationCategory>> childrenByParentId
    ) {
        List<ConsultationCategoryTreeResponse> children = childrenByParentId
                .getOrDefault(category.getId(), List.of())
                .stream()
                .sorted(Comparator.comparingInt(ConsultationCategory::getSortOrder).thenComparing(ConsultationCategory::getId))
                .map(child -> toTreeResponse(child, childrenByParentId))
                .toList();

        return new ConsultationCategoryTreeResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDepth(),
                children
        );
    }
}
