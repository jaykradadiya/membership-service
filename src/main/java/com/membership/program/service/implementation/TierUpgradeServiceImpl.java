package com.membership.program.service.implementation;

import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.evaluation.EvaluationResult;
import com.membership.program.dto.evaluation.TierUpgradeRuleDefinition;
import com.membership.program.entity.TierUpgradeRule;
import com.membership.program.entity.User;
import com.membership.program.repository.TierUpgradeRuleRepository;
import com.membership.program.repository.UserRepository;
import com.membership.program.service.MembershipService;
import com.membership.program.service.TierUpgradeService;
import com.membership.program.service.evaluation.EvaluationContextBuilder;
import com.membership.program.service.evaluation.RuleEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.membership.program.dto.evaluation.CriteriaDefinition;
import java.util.ArrayList;

/**
 * Implementation of the tier upgrade service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TierUpgradeServiceImpl implements TierUpgradeService {
    
    private final UserRepository userRepository;
    private final TierUpgradeRuleRepository tierUpgradeRuleRepository;
    private final EvaluationContextBuilder contextBuilder;
    private final RuleEvaluationService ruleEvaluationService;
    private final MembershipService membershipService;
    
    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResult> evaluateTierUpgrade(Long userId) {
        log.info("Evaluating tier upgrade for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Build evaluation context
        EvaluationContext context = contextBuilder.buildContext(user);
        
        // Get applicable rules
        List<TierUpgradeRuleDefinition> applicableRules = getApplicableRules(userId);
        
        // Find the best rule
        Optional<TierUpgradeRuleDefinition> bestRule = ruleEvaluationService.findBestApplicableRule(applicableRules, context);
        
        if (bestRule.isPresent()) {
            return ruleEvaluationService.evaluateRule(bestRule.get(), context);
        } else {
            log.info("No applicable upgrade rules found for user: {}", userId);
            return List.of();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResult> getDetailedEvaluationResults(Long userId) {
        log.info("Getting detailed evaluation results for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        EvaluationContext context = contextBuilder.buildContext(user);
        List<TierUpgradeRuleDefinition> applicableRules = getApplicableRules(userId);
        
        // Evaluate all applicable rules
        return applicableRules.stream()
                .flatMap(rule -> ruleEvaluationService.evaluateRule(rule, context).stream())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUserEligibleForUpgrade(Long userId) {
        return getBestApplicableRule(userId) != null;
    }
    
    @Override
    @Transactional(readOnly = true)
    public TierUpgradeRuleDefinition getBestApplicableRule(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        EvaluationContext context = contextBuilder.buildContext(user);
        List<TierUpgradeRuleDefinition> applicableRules = getApplicableRules(userId);
        
        Optional<TierUpgradeRuleDefinition> bestRule = ruleEvaluationService.findBestApplicableRule(applicableRules, context);
        return bestRule.orElse(null);
    }
    
    @Override
    public void processAutomaticUpgrades(Long userId) {
        log.info("Processing automatic tier upgrades for user: {}", userId);
        
        TierUpgradeRuleDefinition bestRule = getBestApplicableRule(userId);
        
        if (bestRule != null && bestRule.isAutoUpgrade()) {
            log.info("Auto-upgrading user {} to tier {}", userId, bestRule.getTargetTierId());
            
            try {
                membershipService.upgradeTier(userId, bestRule.getTargetTierId(), true);
                
                // Update user's last evaluation date
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
                user.setLastTierEvaluationDate(java.time.LocalDateTime.now());
                userRepository.save(user);
                
                log.info("Successfully auto-upgraded user {} to tier {}", userId, bestRule.getTargetTierId());
            } catch (Exception e) {
                log.error("Error during auto-upgrade for user {}: {}", userId, e.getMessage());
            }
        } else {
            log.info("No automatic upgrade available for user: {}", userId);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TierUpgradeRuleDefinition> getApplicableRules(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Get rules applicable to the user's current tier
        List<TierUpgradeRule> rules = tierUpgradeRuleRepository.findBySourceTierIdAndActiveTrue(user.getCurrentTierLevel().longValue());
        
        return rules.stream()
                .map(this::mapToRuleDefinition)
                .collect(Collectors.toList());
    }
    
    private TierUpgradeRuleDefinition mapToRuleDefinition(TierUpgradeRule rule) {
        // Build criteria list from the rule's fields
        List<CriteriaDefinition> criteria = new ArrayList<>();
        
        // Add ORDER_COUNT criteria if present
        if (rule.getMinOrdersRequired() != null) {
            criteria.add(CriteriaDefinition.builder()
                    .criteriaType("ORDER_COUNT")
                    .value(rule.getMinOrdersRequired())
                    .logicalCondition("AND")
                    .build());
        }
        
        // Add MONTHLY_ORDER_VALUE criteria if present
        if (rule.getMinMonthlyOrderValue() != null) {
            criteria.add(CriteriaDefinition.builder()
                    .criteriaType("MONTHLY_ORDER_VALUE")
                    .value(rule.getMinMonthlyOrderValue())
                    .logicalCondition("AND")
                    .build());
        }
        
        // Add USER_COHORT criteria if present
        if (rule.getCohortRestriction() != null) {
            criteria.add(CriteriaDefinition.builder()
                    .criteriaType("USER_COHORT")
                    .value(rule.getCohortRestriction())
                    .logicalCondition("AND")
                    .build());
        }
        
        return TierUpgradeRuleDefinition.builder()
                .id(rule.getId())
                .ruleName(rule.getRuleName())
                .ruleDescription(rule.getRuleDescription())
                .sourceTierId(rule.getSourceTier().getId())
                .targetTierId(rule.getTargetTier().getId())
                .autoUpgrade(rule.isAutoUpgrade())
                .active(rule.isActive())
                .criteria(criteria)
                .build();
    }
}
