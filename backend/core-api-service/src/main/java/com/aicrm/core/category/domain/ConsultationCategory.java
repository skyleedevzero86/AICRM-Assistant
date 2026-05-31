package com.aicrm.core.category.domain;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultation_categories")
public class ConsultationCategory {

    private static final short LEAF_DEPTH = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ConsultationCategory parent;

    @OneToMany(mappedBy = "parent")
    private List<ConsultationCategory> children = new ArrayList<>();

    @Column(nullable = false)
    private short depth = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected ConsultationCategory() {
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public ConsultationCategory getParent() {
        return parent;
    }

    public List<ConsultationCategory> getChildren() {
        return children;
    }

    public short getDepth() {
        return depth;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isLeaf() {
        return depth == LEAF_DEPTH;
    }

    public void ensureLeafCategory() {
        if (!isLeaf()) {
            throw new BusinessException(
                    ErrorCode.INVALID_CATEGORY_DEPTH,
                    "Consultation category must be a depth-3 leaf category: " + id
            );
        }
    }
}
