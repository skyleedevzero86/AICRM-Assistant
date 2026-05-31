package com.aicrm.core.customer.infrastructure;

import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCustomerRepository implements CustomerRepository {

    private final CustomerSpringDataJpaRepository springDataJpaRepository;

    public JpaCustomerRepository(CustomerSpringDataJpaRepository springDataJpaRepository) {
        this.springDataJpaRepository = springDataJpaRepository;
    }

    @Override
    public Customer getById(Long customerId) {
        return springDataJpaRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "Customer not found: " + customerId
                ));
    }
}
