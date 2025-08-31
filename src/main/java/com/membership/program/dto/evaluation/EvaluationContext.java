package com.membership.program.dto.evaluation;

import com.membership.program.entity.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Simplified context information for evaluating tier upgrade criteria
 */
@Data
@Builder
public class EvaluationContext {
    
    private Long userId;
    private User user;
    
    // Required metrics for the three rules
    private Integer totalOrderCount;
    private BigDecimal monthlyOrderValue;
    private String userCohort;
}
