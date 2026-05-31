package com.aicrm.core.customer.domain;

public interface CustomerRepository {

    Customer getById(Long customerId);
}
