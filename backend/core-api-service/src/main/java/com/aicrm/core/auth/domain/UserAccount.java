package com.aicrm.core.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "users")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "withdrawn_yn", nullable = false, columnDefinition = "char(1)")
    private String withdrawnYn = "N";

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "suspended_yn", nullable = false, columnDefinition = "char(1)")
    private String suspendedYn = "N";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected UserAccount() {
    }

    public static UserAccount create(String email, String passwordHash, String name, UserRole role) {
        UserAccount account = new UserAccount();
        account.email = email;
        account.passwordHash = passwordHash;
        account.name = name;
        account.role = role;
        account.createdAt = Instant.now();
        account.updatedAt = Instant.now();
        return account;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public String getWithdrawnYn() {
        return withdrawnYn == null ? "N" : withdrawnYn.trim();
    }

    public String getSuspendedYn() {
        return suspendedYn == null ? "N" : suspendedYn.trim();
    }

    public boolean isWithdrawn() {
        return "Y".equals(getWithdrawnYn());
    }

    public boolean isSuspended() {
        return "Y".equals(getSuspendedYn());
    }

    public void setSuspendedYn(String suspendedYn) {
        this.suspendedYn = suspendedYn == null ? "N" : suspendedYn.trim();
        this.updatedAt = Instant.now();
    }

    public void setWithdrawnYn(String withdrawnYn) {
        this.withdrawnYn = withdrawnYn == null ? "N" : withdrawnYn.trim();
        this.updatedAt = Instant.now();
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = Instant.now();
    }

    public void updateEmail(String email) {
        this.email = email.trim().toLowerCase();
        this.updatedAt = Instant.now();
    }
}
