package com.aicrm.core.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.time.Instant;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Customer() {
    }

    public static Customer create(String name, String phone, String email) {
        Customer customer = new Customer();
        customer.name = name;
        customer.phone = phone;
        customer.email = email;
        customer.createdAt = Instant.now();
        customer.updatedAt = Instant.now();
        return customer;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void updateProfile(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이름이 필요합니다");
        }
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이메일이 필요합니다");
        }
        this.name = name.trim();
        this.email = email.trim();
        this.updatedAt = Instant.now();
    }
}
