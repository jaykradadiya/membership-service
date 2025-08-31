package com.membership.program.repository;

import com.membership.program.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    /**
     * Find active membership plans
     */
    List<MembershipPlan> findByActiveTrue();

    /**
     * Find membership plans applicable for a specific tier level
     */
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.active = true AND mp.maxTierLevel = :tierLevel")
    List<MembershipPlan> findApplicablePlansForTier(@Param("tierLevel") Integer tierLevel);

    /**
     * Find membership plans with discounts
     */
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.active = true AND mp.discountPercentage IS NOT NULL AND mp.discountPercentage > 0")
    List<MembershipPlan> findPlansWithDiscounts();

}
