package com.membership.program.service.evaluation;

import com.membership.program.dto.evaluation.TierUpgradeRuleDefinition;
import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.evaluation.EvaluationResult;

import java.util.List;
import java.util.Optional;

/**
 * Service for evaluating tier upgrade rules
 */
public interface RuleEvaluationService {
    
    /**
     * Evaluate a single rule against the given context
     */
    List<EvaluationResult> evaluateRule(TierUpgradeRuleDefinition rule, EvaluationContext context);
    
    /**
     * Check if a rule passes all criteria
     */
    boolean isRuleEligible(TierUpgradeRuleDefinition rule, EvaluationContext context);
    
    /**
     * Find the best applicable rule for a user
     */
    Optional<TierUpgradeRuleDefinition> findBestApplicableRule(List<TierUpgradeRuleDefinition> rules, EvaluationContext context);
    
    /**
     * Get detailed evaluation results for a rule
     */
    List<EvaluationResult> getDetailedEvaluationResults(TierUpgradeRuleDefinition rule, EvaluationContext context);
}
