package com.membership.program.dto.evaluation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Simplified definition of a tier upgrade rule with multiple criteria
 */
@Data
@Builder
public class TierUpgradeRuleDefinition {
    
    private Long id;
    private String ruleName;
    private String ruleDescription;
    private Long sourceTierId;
    private Long targetTierId;
    private boolean autoUpgrade;
    private boolean active;
    private List<CriteriaDefinition> criteria;
}
