package com.aicrm.core.category.domain;

import java.util.List;

public interface ConsultationCategoryRepository {

    ConsultationCategory getEnabledLeafCategory(Long categoryId);

    List<ConsultationCategory> findAllActiveOrdered();

    boolean existsActiveChild(Long categoryId);
}
