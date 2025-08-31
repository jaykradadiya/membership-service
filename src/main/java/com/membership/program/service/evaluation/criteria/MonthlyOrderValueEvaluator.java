package com.membership.program.service.evaluation.criteria;

import com.membership.program.service.evaluation.CriteriaEvaluator;
import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.evaluation.EvaluationResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Evaluator for monthly order value criteria
 */
@Component
public class MonthlyOrderValueEvaluator implements CriteriaEvaluator {
    
    @Override
    public String getCriteriaType() {
        return "MONTHLY_ORDER_VALUE";
    }
    
    @Override
    public EvaluationResult evaluate(EvaluationContext context, Object criteriaValue) {
        if (!(criteriaValue instanceof Number)) {
            return EvaluationResult.failed(getCriteriaType(), criteriaValue, "Invalid value type", 
                    "Criteria value must be a number");
        }
        
        BigDecimal expectedValue = new BigDecimal(criteriaValue.toString());
        BigDecimal actualValue = context.getMonthlyOrderValue();
        
        if (actualValue == null) {
            actualValue = BigDecimal.ZERO;
        }
        
        if (actualValue.compareTo(expectedValue) >= 0) {
            return EvaluationResult.passed(getCriteriaType(), expectedValue, actualValue);
        } else {
            return EvaluationResult.failed(getCriteriaType(), expectedValue, actualValue, 
                    String.format("Required: %s, Actual: %s", expectedValue, actualValue));
        }
    }
    
    @Override
    public boolean canHandle(String criteriaType) {
        return "MONTHLY_ORDER_VALUE".equals(criteriaType);
    }
}
