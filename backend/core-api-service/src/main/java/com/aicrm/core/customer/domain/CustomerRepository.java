package com.aicrm.core.customer.domain;

import java.util.Optional;

public interface CustomerRepository {

    Customer getById(Long customerId);

    Optional<Customer> findByPhone(String phone);

    Customer save(Customer customer);
}
