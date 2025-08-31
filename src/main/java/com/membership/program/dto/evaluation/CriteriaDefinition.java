package com.membership.program.dto.evaluation;

import lombok.Builder;
import lombok.Data;

/**
 * Simplified definition of a single evaluation criteria
 */
@Data
@Builder
public class CriteriaDefinition {
    
    private String criteriaType; // ORDER_COUNT, MONTHLY_ORDER_VALUE, USER_COHORT
    private Object value; // The threshold value to compare against
    private String logicalCondition; // AND, OR (for combining multiple criteria)
}
