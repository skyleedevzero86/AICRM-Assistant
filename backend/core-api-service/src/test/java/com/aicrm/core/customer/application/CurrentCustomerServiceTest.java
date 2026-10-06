package com.aicrm.core.customer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrentCustomerServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CurrentCustomerService currentCustomerService;

    @Test
    void resolveCustomerIdMapsLoggedInUserToCustomer() {
        // given
        when(currentUserProvider.require()).thenReturn(customerUser(100L));
        when(customerRepository.findByUserId(100L)).thenReturn(Optional.of(customer(1L)));

        // when
        Optional<Long> customerId = currentCustomerService.resolveCustomerId();

        // then
        assertThat(customerId).contains(1L);
    }

    @Test
    void requireCustomerIdRejectsNonCustomerRole() {
        // given
        when(currentUserProvider.require()).thenReturn(new AuthenticatedUser(1L, "agent@test.com", "상담원", UserRole.AGENT));

        // when & then
        assertThatThrownBy(() -> currentCustomerService.requireCustomerId())
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    private AuthenticatedUser customerUser(Long userId) {
        return new AuthenticatedUser(userId, "customer@test.com", "김고객", UserRole.CUSTOMER);
    }

    private Customer customer(Long id) {
        try {
            var constructor = Customer.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Customer customer = constructor.newInstance();
            var idField = Customer.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(customer, id);
            return customer;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
