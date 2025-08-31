package com.membership.program.repository;

import com.membership.program.entity.TierUpgradeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierUpgradeRuleRepository extends JpaRepository<TierUpgradeRule, Long> {

    /**
     * Find active rules by source tier ID
     */
    List<TierUpgradeRule> findBySourceTierIdAndActiveTrue(Long sourceTierId);

}
