package com.membership.program.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membership_plans", indexes = {
        @Index(name = "idx_plans_name", columnList = "name"),
        @Index(name = "idx_plans_duration", columnList = "duration_months"),
        @Index(name = "idx_plans_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"subscriptions"})
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Plan name is required")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Duration in months is required")
    @Positive(message = "Duration must be positive")
    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "max_tier_level")
    private Integer maxTierLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    // Business logic methods
    public BigDecimal getDiscountedPrice() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return price;
        }
        return price.multiply(BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100))));
    }

    public boolean isApplicableForTier(Integer tierLevel) {
        return maxTierLevel == null || tierLevel <= maxTierLevel;
    }
}
