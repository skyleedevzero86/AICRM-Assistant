package com.aicrm.core.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.category.domain.ConsultationCategoryRepository;
import com.aicrm.core.category.dto.ConsultationCategoryTreeResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetConsultationCategoryTreeServiceTest {

    @Mock
    private ConsultationCategoryRepository consultationCategoryRepository;

    @InjectMocks
    private GetConsultationCategoryTreeService getConsultationCategoryTreeService;

    private ConsultationCategory deliveryRoot;
    private ConsultationCategory deliveryStatus;
    private ConsultationCategory deliveryDelay;
    private ConsultationCategory orderPaymentRoot;
    private ConsultationCategory payment;
    private ConsultationCategory paymentFail;

    @BeforeEach
    void setUp() {
        deliveryRoot = category(1L, "DELIVERY", "배송", null, (short) 1, 2);
        deliveryStatus = category(2L, "DELIVERY_STATUS", "배송 상태", deliveryRoot, (short) 2, 1);
        deliveryDelay = category(3L, "DELIVERY_DELAY", "배송 지연", deliveryStatus, (short) 3, 1);
        orderPaymentRoot = category(4L, "ORDER_PAYMENT", "주문/결제", null, (short) 1, 1);
        payment = category(5L, "PAYMENT", "결제", orderPaymentRoot, (short) 2, 2);
        paymentFail = category(6L, "PAYMENT_FAIL", "결제 실패", payment, (short) 3, 1);
    }

    @Test
    void getActiveTreeBuildsLargeMediumSmallHierarchy() {
        // given
        when(consultationCategoryRepository.findAllActiveOrdered())
                .thenReturn(List.of(
                        orderPaymentRoot,
                        deliveryRoot,
                        payment,
                        deliveryStatus,
                        paymentFail,
                        deliveryDelay
                ));

        // when
        List<ConsultationCategoryTreeResponse> tree = getConsultationCategoryTreeService.getActiveTree();

        // then
        assertThat(tree).hasSize(2);
        assertThat(tree.get(0).code()).isEqualTo("ORDER_PAYMENT");
        assertThat(tree.get(0).children().get(0).children().get(0).code()).isEqualTo("PAYMENT_FAIL");
        assertThat(tree.get(1).code()).isEqualTo("DELIVERY");
        assertThat(tree.get(1).children().get(0).children().get(0).code()).isEqualTo("DELIVERY_DELAY");
        assertThat(tree.get(1).children().get(0).children().get(0).depth()).isEqualTo(3);
    }

    private ConsultationCategory category(
            Long id,
            String code,
            String name,
            ConsultationCategory parent,
            short depth,
            int sortOrder
    ) {
        try {
            var constructor = ConsultationCategory.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            ConsultationCategory category = constructor.newInstance();
            var idField = ConsultationCategory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, id);

            var codeField = ConsultationCategory.class.getDeclaredField("code");
            codeField.setAccessible(true);
            codeField.set(category, code);

            var nameField = ConsultationCategory.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(category, name);

            var parentField = ConsultationCategory.class.getDeclaredField("parent");
            parentField.setAccessible(true);
            parentField.set(category, parent);

            var depthField = ConsultationCategory.class.getDeclaredField("depth");
            depthField.setAccessible(true);
            depthField.set(category, depth);

            var sortOrderField = ConsultationCategory.class.getDeclaredField("sortOrder");
            sortOrderField.setAccessible(true);
            sortOrderField.set(category, sortOrder);
            return category;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
