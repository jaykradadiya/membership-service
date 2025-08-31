package com.membership.program.service;

import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.evaluation.EvaluationResult;
import com.membership.program.dto.evaluation.TierUpgradeRuleDefinition;

import java.util.List;

/**
 * Service for managing tier upgrades using the rule-based system
 */
public interface TierUpgradeService {
    
    /**
     * Evaluate tier upgrade eligibility for a user
     */
    List<EvaluationResult> evaluateTierUpgrade(Long userId);
    
    /**
     * Get detailed evaluation results for a user
     */
    List<EvaluationResult> getDetailedEvaluationResults(Long userId);
    
    /**
     * Check if a user is eligible for any tier upgrade
     */
    boolean isUserEligibleForUpgrade(Long userId);
    
    /**
     * Get the best applicable upgrade rule for a user
     */
    TierUpgradeRuleDefinition getBestApplicableRule(Long userId);
    
    /**
     * Process automatic tier upgrades for a user
     */
    void processAutomaticUpgrades(Long userId);
    
    /**
     * Get all applicable rules for a user
     */
    List<TierUpgradeRuleDefinition> getApplicableRules(Long userId);
}
