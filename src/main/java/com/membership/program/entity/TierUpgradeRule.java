package com.membership.program.entity;

import com.membership.program.dto.enums.UpgradeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tier_upgrade_rules", indexes = {
        @Index(name = "idx_rules_source_tier", columnList = "source_tier_id"),
        @Index(name = "idx_rules_target_tier", columnList = "target_tier_id"),
        @Index(name = "idx_rules_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"sourceTier", "targetTier"})
public class TierUpgradeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_tier_id", nullable = false)
    @NotNull(message = "Source tier is required")
    private MembershipTier sourceTier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_tier_id", nullable = false)
    @NotNull(message = "Target tier is required")
    private MembershipTier targetTier;

    @Column(name = "rule_name", nullable = false, length = 200)
    @NotNull(message = "Rule name is required")
    private String ruleName;

    @Column(name = "rule_description", length = 1000)
    private String ruleDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "upgrade_type", nullable = false, length = 50)
    private UpgradeType upgradeType;

    @Column(name = "min_orders_required",nullable = true)
    @Positive(message = "Minimum orders must be positive")
    private Integer minOrdersRequired;

    @Column(name = "min_monthly_order_value", precision = 10, scale = 2, nullable = true)
    private BigDecimal minMonthlyOrderValue;

    @Column(name = "min_membership_duration_days")
    @Positive(message = "Minimum membership duration must be positive")
    private Integer minMembershipDurationDays;

    @Column(name = "cohort_restriction", length = 100,nullable = true)
    private String cohortRestriction;

    @Column(name = "evaluation_frequency_days", nullable = false)
    @Positive(message = "Evaluation frequency must be positive")
    private Integer evaluationFrequencyDays = 30;

    @Column(name = "auto_upgrade", nullable = false)
    private boolean autoUpgrade = false;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business logic methods
    public boolean isEligibleForUpgrade(Integer orderCount, BigDecimal monthlyOrderValue, 
                                      Integer membershipDurationDays, String userCohort) {
        if (!active) {
            return false;
        }

        if (minOrdersRequired != null && orderCount < minOrdersRequired) {
            return false;
        }

        if (minMonthlyOrderValue != null && monthlyOrderValue.compareTo(minMonthlyOrderValue) < 0) {
            return false;
        }

        if (minMembershipDurationDays != null && membershipDurationDays < minMembershipDurationDays) {
            return false;
        }

        if (cohortRestriction != null && !cohortRestriction.equals(userCohort)) {
            return false;
        }

        return true;
    }
}
