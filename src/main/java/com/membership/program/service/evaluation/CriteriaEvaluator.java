package com.membership.program.service.evaluation;

import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.evaluation.EvaluationResult;

/**
 * Interface for evaluating specific criteria types.
 * This allows for easy extension of new evaluation criteria.
 */
public interface CriteriaEvaluator {
    
    /**
     * Get the type of criteria this evaluator handles
     */
    String getCriteriaType();
    
    /**
     * Evaluate the criteria against the given context
     */
    EvaluationResult evaluate(EvaluationContext context, Object criteriaValue);
    
    /**
     * Check if this evaluator can handle the given criteria type
     */
    boolean canHandle(String criteriaType);
}
