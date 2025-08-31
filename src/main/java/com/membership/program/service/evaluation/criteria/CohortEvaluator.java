package com.membership.program.service.evaluation.criteria;

import com.membership.program.service.evaluation.CriteriaEvaluator;
import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.evaluation.EvaluationResult;
import org.springframework.stereotype.Component;

/**
 * Evaluator for user cohort criteria
 */
@Component
public class CohortEvaluator implements CriteriaEvaluator {
    
    @Override
    public String getCriteriaType() {
        return "USER_COHORT";
    }
    
    @Override
    public EvaluationResult evaluate(EvaluationContext context, Object criteriaValue) {
        if (!(criteriaValue instanceof String)) {
            return EvaluationResult.failed(getCriteriaType(), criteriaValue, "Invalid value type", 
                    "Criteria value must be a string");
        }
        
        String expectedCohort = (String) criteriaValue;
        String actualCohort = context.getUserCohort();
        
        if (actualCohort == null) {
            return EvaluationResult.failed(getCriteriaType(), expectedCohort, "NULL", 
                    "User cohort is not set");
        }
        
        if (expectedCohort.equals(actualCohort)) {
            return EvaluationResult.passed(getCriteriaType(), expectedCohort, actualCohort);
        } else {
            return EvaluationResult.failed(getCriteriaType(), expectedCohort, actualCohort, 
                    String.format("Expected: %s, Actual: %s", expectedCohort, actualCohort));
        }
    }
    
    @Override
    public boolean canHandle(String criteriaType) {
        return "USER_COHORT".equals(criteriaType);
    }
}
