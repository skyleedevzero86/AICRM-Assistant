package com.aicrm.core.category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class ConsultationCategoryTest {

    @Test
    void ensureLeafCategoryAcceptsDepthThree() {
        ConsultationCategory category = category((short) 3);

        category.ensureLeafCategory();

        assertThat(category.isLeaf()).isTrue();
    }

    @Test
    void ensureLeafCategoryRejectsDepthOne() {
        ConsultationCategory category = category((short) 1);

        assertThatThrownBy(category::ensureLeafCategory)
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_CATEGORY_DEPTH);
    }

    @Test
    void ensureLeafCategoryRejectsDepthTwo() {
        ConsultationCategory category = category((short) 2);

        assertThatThrownBy(category::ensureLeafCategory)
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_CATEGORY_DEPTH);
    }

    private ConsultationCategory category(short depth) {
        ConsultationCategory category = new ConsultationCategory();
        try {
            var depthField = ConsultationCategory.class.getDeclaredField("depth");
            depthField.setAccessible(true);
            depthField.set(category, depth);

            var idField = ConsultationCategory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, 1L);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
        return category;
    }
}
