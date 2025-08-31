package com.membership.program.service.evaluation;

import com.membership.program.dto.evaluation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Simplified implementation of the rule evaluation service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEvaluationServiceImpl implements RuleEvaluationService {
    
    private final List<CriteriaEvaluator> criteriaEvaluators;
    
    @Override
    public List<EvaluationResult> evaluateRule(TierUpgradeRuleDefinition rule, EvaluationContext context) {
        log.debug("Evaluating rule: {} for user: {}", rule.getRuleName(), context.getUserId());
        
        return rule.getCriteria().stream()
                .map(criteria -> evaluateCriteria(criteria, context))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isRuleEligible(TierUpgradeRuleDefinition rule, EvaluationContext context) {
        List<EvaluationResult> results = evaluateRule(rule, context);
        
        // Check if all criteria passed
        boolean allPassed = results.stream().allMatch(EvaluationResult::isPassed);
        
        if (allPassed) {
            log.debug("Rule {} passed all criteria for user {}", rule.getRuleName(), context.getUserId());
        } else {
            log.debug("Rule {} failed criteria for user {}. Failed criteria: {}", 
                    rule.getRuleName(), context.getUserId(), 
                    results.stream().filter(r -> !r.isPassed()).map(EvaluationResult::getCriteriaType).collect(Collectors.toList()));
        }
        
        return allPassed;
    }
    
    @Override
    public Optional<TierUpgradeRuleDefinition> findBestApplicableRule(List<TierUpgradeRuleDefinition> rules, EvaluationContext context) {
        log.debug("Finding best applicable rule for user: {}", context.getUserId());
        
        return rules.stream()
                .filter(rule -> rule.isActive())
                .filter(rule -> isRuleEligible(rule, context))
                .max(Comparator.comparing(rule -> rule.getTargetTierId()));
    }
    
    @Override
    public List<EvaluationResult> getDetailedEvaluationResults(TierUpgradeRuleDefinition rule, EvaluationContext context) {
        return evaluateRule(rule, context);
    }
    
    private EvaluationResult evaluateCriteria(CriteriaDefinition criteria, EvaluationContext context) {
        // Find the appropriate evaluator for this criteria type
        Optional<CriteriaEvaluator> evaluator = criteriaEvaluators.stream()
                .filter(e -> e.canHandle(criteria.getCriteriaType()))
                .findFirst();
        
        if (evaluator.isEmpty()) {
            return EvaluationResult.failed(criteria.getCriteriaType(), criteria.getValue(), "UNSUPPORTED", 
                    String.format("No evaluator found for criteria type: %s", criteria.getCriteriaType()));
        }
        
        try {
            return evaluator.get().evaluate(context, criteria.getValue());
        } catch (Exception e) {
            log.error("Error evaluating criteria {}: {}", criteria.getCriteriaType(), e.getMessage());
            return EvaluationResult.failed(criteria.getCriteriaType(), criteria.getValue(), "ERROR", 
                    String.format("Evaluation error: %s", e.getMessage()));
        }
    }
}
