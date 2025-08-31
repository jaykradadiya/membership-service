package com.membership.program.dto.evaluation;

import lombok.Builder;
import lombok.Data;

/**
 * Simplified result of evaluating a single criteria
 */
@Data
@Builder
public class EvaluationResult {
    
    private boolean passed;
    private String criteriaType;
    private Object expectedValue;
    private Object actualValue;
    private String message;
    
    // Helper methods
    public static EvaluationResult passed(String criteriaType, Object expectedValue, Object actualValue) {
        return EvaluationResult.builder()
                .passed(true)
                .criteriaType(criteriaType)
                .expectedValue(expectedValue)
                .actualValue(actualValue)
                .message("Criteria passed")
                .build();
    }
    
    public static EvaluationResult failed(String criteriaType, Object expectedValue, Object actualValue, String details) {
        return EvaluationResult.builder()
                .passed(false)
                .criteriaType(criteriaType)
                .expectedValue(expectedValue)
                .actualValue(actualValue)
                .message("Criteria failed: " + details)
                .build();
    }
}
