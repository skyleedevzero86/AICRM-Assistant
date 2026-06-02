package com.aicrm.core.customer.domain;

import java.util.Optional;

public interface CustomerRepository {

    Customer getById(Long customerId);

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByUserId(Long userId);

    java.util.List<Customer> findAllRegisteredUsers();

    Customer save(Customer customer);
}
