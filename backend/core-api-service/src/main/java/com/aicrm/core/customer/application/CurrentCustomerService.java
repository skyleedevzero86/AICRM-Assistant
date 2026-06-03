package com.aicrm.core.customer.application;

import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CurrentCustomerService {

    private final CurrentUserProvider currentUserProvider;
    private final CustomerRepository customerRepository;

    public CurrentCustomerService(
            CurrentUserProvider currentUserProvider,
            CustomerRepository customerRepository
    ) {
        this.currentUserProvider = currentUserProvider;
        this.customerRepository = customerRepository;
    }

    public Optional<Long> resolveCustomerId() {
        AuthenticatedUser user = requireCustomerUser();
        return customerRepository.findByUserId(user.userId()).map(Customer::getId);
    }

    public Long requireCustomerId() {
        return resolveCustomerId()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "CUSTOMER_NOT_FOUND"));
    }

    public Customer requireCustomer() {
        AuthenticatedUser user = requireCustomerUser();
        return customerRepository.findByUserId(user.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "CUSTOMER_NOT_FOUND", user.userId()));
    }

    private AuthenticatedUser requireCustomerUser() {
        AuthenticatedUser user = currentUserProvider.require();
        if (user.role() != UserRole.CUSTOMER) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return user;
    }
}
