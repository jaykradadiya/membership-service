package com.membership.program.service;

import com.membership.program.dto.request.SubscriptionRequestDTO;
import com.membership.program.dto.response.MembershipPlanResponseDTO;
import com.membership.program.dto.response.SubscriptionHistoryResponseDTO;
import com.membership.program.dto.response.SubscriptionResponseDTO;

import java.util.List;

public interface MembershipService {

    /**
     * Get membership plans with optional filtering
     * @param includeDiscountsOnly if true, returns only plans with discounts; if false, returns all active plans
     * @return List of membership plans
     */
    List<MembershipPlanResponseDTO> getMembershipPlans(Boolean includeDiscountsOnly);


    /**
     * Get membership plans applicable for a user's tier
     */
    List<MembershipPlanResponseDTO> getMembershipPlansForTier(Integer tierLevel);

    /**
     * Subscribe user to a membership plan
     */
    SubscriptionResponseDTO subscribeToPlan(Long userId, SubscriptionRequestDTO request);

    /**
     * Get user's current subscription
     */
    SubscriptionResponseDTO getCurrentSubscription(Long userId);

    /**
     * Get user's subscription history
     */
    List<SubscriptionHistoryResponseDTO> getUserSubscriptionHistory(Long userId);

    /**
     * Cancel user's subscription
     */
    SubscriptionResponseDTO cancelSubscription(Long userId, String reason);

    /**
     * Upgrade user's membership tier
     */
    SubscriptionResponseDTO upgradeTier(Long userId, Long newTierId,boolean isAutoUpgrade);

    /**
     * Downgrade user's membership tier
     */
    SubscriptionResponseDTO downgradeTier(Long userId, Long newTierId,boolean isAutoDowngrade);

    /**
     * Renew user's subscription
     */
    SubscriptionResponseDTO renewSubscription(Long userId);

    /**
     * Get user's membership status and tier information
     */
    SubscriptionResponseDTO getUserMembershipStatus(Long userId);


}