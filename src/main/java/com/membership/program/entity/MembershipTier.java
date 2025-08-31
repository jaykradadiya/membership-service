package com.membership.program.entity;

import jakarta.persistence.*;
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
@Table(name = "membership_tiers", indexes = {
        @Index(name = "idx_tiers_name", columnList = "name"),
        @Index(name = "idx_tiers_level", columnList = "tier_level"),
        @Index(name = "idx_tiers_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"subscriptions", "tierUpgradeRules"})
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tier name is required")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Tier level is required")
    @Positive(message = "Tier level must be positive")
    @Column(name = "tier_level", nullable = false, unique = true)
    private Integer tierLevel;

    @Column(name = "min_orders_required")
    private Integer minOrdersRequired;

    @Column(name = "min_monthly_order_value", precision = 10, scale = 2)
    private BigDecimal minMonthlyOrderValue;

    @Column(name = "cohort_restriction", length = 100)
    private String cohortRestriction;

    @Column(name = "benefits_description", length = 1000)
    private String benefitsDescription;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "targetTier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TierUpgradeRule> tierUpgradeRules = new ArrayList<>();

    // Business logic methods
    public boolean isEligible(Integer orderCount, BigDecimal monthlyOrderValue, String userCohort) {
        if (!active) {
            return false;
        }

        if (minOrdersRequired != null && orderCount < minOrdersRequired) {
            return false;
        }

        if (minMonthlyOrderValue != null && monthlyOrderValue.compareTo(minMonthlyOrderValue) < 0) {
            return false;
        }

        if (cohortRestriction != null && !cohortRestriction.equals(userCohort)) {
            return false;
        }

        return true;
    }

    public boolean canUpgradeFrom(MembershipTier currentTier) {
        return this.tierLevel > currentTier.getTierLevel();
    }
}
