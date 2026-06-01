package com.aicrm.core.category.infrastructure;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.category.domain.ConsultationCategoryRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaConsultationCategoryRepository implements ConsultationCategoryRepository {

    private final ConsultationCategorySpringDataJpaRepository springDataJpaRepository;

    public JpaConsultationCategoryRepository(ConsultationCategorySpringDataJpaRepository springDataJpaRepository) {
        this.springDataJpaRepository = springDataJpaRepository;
    }

    @Override
    public ConsultationCategory getEnabledLeafCategory(Long categoryId) {
        ConsultationCategory category = springDataJpaRepository.findByIdAndActiveTrue(categoryId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "상담 카테고리를 찾을 수 없습니다: " + categoryId
                ));

        category.ensureLeafCategory();

        if (existsActiveChild(categoryId)) {
            throw new BusinessException(
                    ErrorCode.INVALID_CATEGORY_DEPTH,
                    "상담 카테고리는 3단계 리프 카테고리여야 합니다: " + categoryId
            );
        }

        return category;
    }

    @Override
    public List<ConsultationCategory> findAllActiveOrdered() {
        return springDataJpaRepository.findByActiveTrueOrderBySortOrderAscIdAsc();
    }

    @Override
    public boolean existsActiveChild(Long categoryId) {
        return springDataJpaRepository.existsByParentIdAndActiveTrue(categoryId);
    }
}
