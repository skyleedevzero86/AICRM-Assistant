package com.aicrm.core.customer.infrastructure;

import com.aicrm.core.customer.domain.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSpringDataJpaRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);
}
