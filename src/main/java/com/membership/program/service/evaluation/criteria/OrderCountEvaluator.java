package com.membership.program.service.evaluation.criteria;

import com.membership.program.service.evaluation.CriteriaEvaluator;
import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.evaluation.EvaluationResult;
import org.springframework.stereotype.Component;

/**
 * Evaluator for order count criteria
 */
@Component
public class OrderCountEvaluator implements CriteriaEvaluator {
    
    @Override
    public String getCriteriaType() {
        return "ORDER_COUNT";
    }
    
    @Override
    public EvaluationResult evaluate(EvaluationContext context, Object criteriaValue) {
        if (!(criteriaValue instanceof Number)) {
            return EvaluationResult.failed(getCriteriaType(), criteriaValue, "Invalid value type", 
                    "Criteria value must be a number");
        }
        
        int expectedCount = ((Number) criteriaValue).intValue();
        int actualCount = context.getTotalOrderCount();
        
        if (actualCount >= expectedCount) {
            return EvaluationResult.passed(getCriteriaType(), expectedCount, actualCount);
        } else {
            return EvaluationResult.failed(getCriteriaType(), expectedCount, actualCount, 
                    String.format("Required: %d, Actual: %d", expectedCount, actualCount));
        }
    }
    
    @Override
    public boolean canHandle(String criteriaType) {
        return "ORDER_COUNT".equals(criteriaType);
    }
}
