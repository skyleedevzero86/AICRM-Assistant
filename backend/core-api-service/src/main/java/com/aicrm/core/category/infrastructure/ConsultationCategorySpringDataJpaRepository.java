package com.aicrm.core.category.infrastructure;

import com.aicrm.core.category.domain.ConsultationCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationCategorySpringDataJpaRepository extends JpaRepository<ConsultationCategory, Long> {

    List<ConsultationCategory> findByActiveTrueOrderBySortOrderAscIdAsc();

    Optional<ConsultationCategory> findByIdAndActiveTrue(Long id);

    boolean existsByParentIdAndActiveTrue(Long parentId);
}
