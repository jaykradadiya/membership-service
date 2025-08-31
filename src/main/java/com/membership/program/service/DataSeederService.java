package com.membership.program.service;

import com.membership.program.entity.MembershipPlan;
import com.membership.program.entity.MembershipTier;
import com.membership.program.entity.TierUpgradeRule;
import com.membership.program.dto.enums.UpgradeType;
import com.membership.program.repository.MembershipPlanRepository;
import com.membership.program.repository.MembershipTierRepository;
import com.membership.program.repository.TierUpgradeRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class DataSeederService implements CommandLineRunner {

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final TierUpgradeRuleRepository tierUpgradeRuleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data seeding process...");
        
        try {
            seedMembershipTiers();
            seedMembershipPlans();
            seedTierUpgradeRules();
            log.info("Data seeding completed successfully!");
        } catch (Exception e) {
            log.error("Error during data seeding: {}", e.getMessage(), e);
        }
    }

    private void seedMembershipTiers() {
        log.info("Seeding membership tiers...");
        
        if (membershipTierRepository.count() > 0) {
            log.info("Membership tiers already exist, skipping...");
            return;
        }

        List<MembershipTier> tiers = Arrays.asList(
            // Silver Tier - Basic membership
            MembershipTier.builder()
                .name("Silver")
                .description("Basic membership tier with essential benefits")
                .tierLevel(1)
                .minOrdersRequired(0)
                .minMonthlyOrderValue(BigDecimal.ZERO)
                .cohortRestriction(null)
                .benefitsDescription("Free shipping on orders over $50, 5% discount on selected items, Priority customer support")
                .discountPercentage(BigDecimal.valueOf(5))
                .active(true)
                .build(),

            // Gold Tier - Mid-level membership
            MembershipTier.builder()
                .name("Gold")
                .description("Premium membership tier with enhanced benefits")
                .tierLevel(2)
                .minOrdersRequired(5)
                .minMonthlyOrderValue(BigDecimal.valueOf(200))
                .cohortRestriction(null)
                .benefitsDescription("Free shipping on all orders, 10% discount on all items, VIP customer support, Early access to sales, Birthday rewards")
                .discountPercentage(BigDecimal.valueOf(10))
                .active(true)
                .build(),

            // Platinum Tier - Elite membership
            MembershipTier.builder()
                .name("Platinum")
                .description("Elite membership tier with exclusive benefits")
                .tierLevel(3)
                .minOrdersRequired(10)
                .minMonthlyOrderValue(BigDecimal.valueOf(500))
                .cohortRestriction(null)
                .benefitsDescription("Free shipping on all orders, 15% discount on all items, 24/7 VIP customer support, Exclusive product access, Personal shopping assistant, Invitation to exclusive events, Quarterly gift box")
                .discountPercentage(BigDecimal.valueOf(15))
                .active(true)
                .build()
        );

        membershipTierRepository.saveAll(tiers);
        log.info("Successfully seeded {} membership tiers", tiers.size());
    }

    private void seedMembershipPlans() {
        log.info("Seeding membership plans...");
        
        if (membershipPlanRepository.count() > 0) {
            log.info("Membership plans already exist, skipping...");
            return;
        }

        List<MembershipPlan> plans = Arrays.asList(
            // Monthly Plans
            MembershipPlan.builder()
                .name("Monthly Silver")
                .description("Monthly subscription to Silver tier membership")
                .durationMonths(1)
                .price(BigDecimal.valueOf(19.99))
                .discountPercentage(BigDecimal.ZERO)
                .maxTierLevel(1)
                .active(true)
                .build(),

            MembershipPlan.builder()
                .name("Monthly Gold")
                .description("Monthly subscription to Gold tier membership")
                .durationMonths(1)
                .price(BigDecimal.valueOf(39.99))
                .discountPercentage(BigDecimal.valueOf(5))
                .maxTierLevel(2)
                .active(true)
                .build(),

            MembershipPlan.builder()
                .name("Monthly Platinum")
                .description("Monthly subscription to Platinum tier membership")
                .durationMonths(1)
                .price(BigDecimal.valueOf(79.99))
                .discountPercentage(BigDecimal.valueOf(10))
                .maxTierLevel(3)
                .active(true)
                .build(),

            // Quarterly Plans (with quarterly discount)
            MembershipPlan.builder()
                .name("Quarterly Silver")
                .description("Quarterly subscription to Silver tier membership with savings")
                .durationMonths(3)
                .price(BigDecimal.valueOf(59.97))
                .discountPercentage(BigDecimal.valueOf(10))
                .maxTierLevel(1)
                .active(true)
                .build(),

            MembershipPlan.builder()
                .name("Quarterly Gold")
                .description("Quarterly subscription to Gold tier membership with savings")
                .durationMonths(3)
                .price(BigDecimal.valueOf(119.97))
                .discountPercentage(BigDecimal.valueOf(15))
                .maxTierLevel(2)
                .active(true)
                .build(),

            MembershipPlan.builder()
                .name("Quarterly Platinum")
                .description("Quarterly subscription to Platinum tier membership with savings")
                .durationMonths(3)
                .price(BigDecimal.valueOf(239.97))
                .discountPercentage(BigDecimal.valueOf(20))
                .maxTierLevel(3)
                .active(true)
                .build(),

            // Yearly Plans (with annual discount)
            MembershipPlan.builder()
                .name("Yearly Silver")
                .description("Annual subscription to Silver tier membership with maximum savings")
                .durationMonths(12)
                .price(BigDecimal.valueOf(239.88))
                .discountPercentage(BigDecimal.valueOf(20))
                .maxTierLevel(1)
                .active(true)
                .build(),

            MembershipPlan.builder()
                .name("Yearly Gold")
                .description("Annual subscription to Gold tier membership with maximum savings")
                .durationMonths(12)
                .price(BigDecimal.valueOf(479.88))
                .discountPercentage(BigDecimal.valueOf(25))
                .maxTierLevel(2)
                .active(true)
                .build(),

            MembershipPlan.builder()
                .name("Yearly Platinum")
                .description("Annual subscription to Platinum tier membership with maximum savings")
                .durationMonths(12)
                .price(BigDecimal.valueOf(959.88))
                .discountPercentage(BigDecimal.valueOf(30))
                .maxTierLevel(3)
                .active(true)
                .build()
        );

        membershipPlanRepository.saveAll(plans);
        log.info("Successfully seeded {} membership plans", plans.size());
    }

    private void seedTierUpgradeRules() {
        log.info("Seeding tier upgrade rules...");
        
        if (tierUpgradeRuleRepository.count() > 0) {
            log.info("Tier upgrade rules already exist, skipping...");
            return;
        }

        // Get the seeded tiers to reference them
        List<MembershipTier> tiers = membershipTierRepository.findAll();
        if (tiers.size() < 3) {
            log.warn("Not enough tiers found for upgrade rules, skipping...");
            return;
        }

        // Find specific tiers by name
        MembershipTier silverTier = tiers.stream()
            .filter(tier -> "Silver".equals(tier.getName()))
            .findFirst()
            .orElse(null);
        
        MembershipTier goldTier = tiers.stream()
            .filter(tier -> "Gold".equals(tier.getName()))
            .findFirst()
            .orElse(null);
        
        MembershipTier platinumTier = tiers.stream()
            .filter(tier -> "Platinum".equals(tier.getName()))
            .findFirst()
            .orElse(null);

        if (silverTier == null || goldTier == null || platinumTier == null) {
            log.warn("Required tiers not found for upgrade rules, skipping...");
            return;
        }

        List<TierUpgradeRule> rules = Arrays.asList(
            // Silver to Gold upgrade rule
            TierUpgradeRule.builder()
                .sourceTier(silverTier)
                .targetTier(goldTier)
                .ruleName("Silver to Gold Auto-Upgrade")
                .ruleDescription("Automatic upgrade from Silver to Gold tier based on order activity")
                .upgradeType(UpgradeType.AUTOMATIC)
                .minOrdersRequired(5)
                .minMonthlyOrderValue(BigDecimal.valueOf(200.00))
                .minMembershipDurationDays(30)
                .cohortRestriction(null)
                .evaluationFrequencyDays(30)
                .autoUpgrade(true)
                .requiresApproval(false)
                .active(true)
                .build(),

            // Gold to Platinum upgrade rule  
            TierUpgradeRule.builder()
                .sourceTier(goldTier)
                .targetTier(platinumTier)
                .ruleName("Gold to Platinum Auto-Upgrade")
                .ruleDescription("Automatic upgrade from Gold to Platinum tier based on order activity")
                .upgradeType(UpgradeType.AUTOMATIC)
                .minOrdersRequired(10)
                .minMonthlyOrderValue(BigDecimal.valueOf(500.00))
                .minMembershipDurationDays(60)
                .cohortRestriction(null)
                .evaluationFrequencyDays(30)
                .autoUpgrade(true)
                .requiresApproval(false)
                .active(true)
                .build(),

            // Silver to Platinum direct upgrade rule
            TierUpgradeRule.builder()
                .sourceTier(silverTier)
                .targetTier(platinumTier)
                .ruleName("Silver to Platinum Direct Upgrade")
                .ruleDescription("Direct upgrade from Silver to Platinum for high-value customers")
                .upgradeType(UpgradeType.PERFORMANCE_BASED)
                .minOrdersRequired(15)
                .minMonthlyOrderValue(BigDecimal.valueOf(1000.00))
                .minMembershipDurationDays(90)
                .cohortRestriction("VIP")
                .evaluationFrequencyDays(30)
                .autoUpgrade(false)
                .requiresApproval(true)
                .active(true)
                .build()
        );

        tierUpgradeRuleRepository.saveAll(rules);
        log.info("Successfully seeded {} tier upgrade rules", rules.size());
    }
}
