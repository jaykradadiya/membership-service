package com.membership.program.controller;

import com.membership.program.dto.request.SubscriptionRequestDTO;
import com.membership.program.dto.response.MembershipPlanResponseDTO;
import com.membership.program.dto.response.NoDataResponse;
import com.membership.program.dto.response.SubscriptionHistoryResponseDTO;
import com.membership.program.dto.response.SubscriptionResponseDTO;
import com.membership.program.service.MembershipService;
import com.membership.program.utility.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.membership.program.constants.ApiEndpoints;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.Membership.BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class MembershipController {

    private final MembershipService membershipService;
    private final SecurityContextUtil securityContextUtil;

    /**
     * Get membership plans with optional filtering
     * @param includeDiscountsOnly if true, returns only plans with discounts; if false or null, returns all active plans
     */
    @GetMapping(ApiEndpoints.Membership.PLANS)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMembershipPlans(
            @RequestParam(required = false) Boolean includeDiscountsOnly) {
        
        if (includeDiscountsOnly != null && includeDiscountsOnly) {
            log.info("Fetching membership plans with discounts");
        } else {
            log.info("Fetching all membership plans");
        }
        
        List<MembershipPlanResponseDTO> plans = membershipService.getMembershipPlans(includeDiscountsOnly);
        
        if (plans.isEmpty()) {
            String message = includeDiscountsOnly != null && includeDiscountsOnly 
                ? "No discounted membership plans found" 
                : "No membership plans found";
            NoDataResponse noDataResponse = NoDataResponse.create(message, "/api/v1/membership/plans");
            return ResponseEntity.ok(noDataResponse);
        }
        
        return ResponseEntity.ok(plans);
    }

    /**
     * Get membership plans for a specific tier level
     */
    @GetMapping(ApiEndpoints.Membership.PLANS_TIER)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMembershipPlansForTier(
            @PathVariable Integer tierLevel) {
        log.info("Fetching membership plans for tier level: {}", tierLevel);
        List<MembershipPlanResponseDTO> plans = membershipService.getMembershipPlansForTier(tierLevel);
        
        if (plans.isEmpty()) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No membership plans found for tier level " + tierLevel,
                "/api/v1/membership/plans/tier/" + tierLevel
            );
            return ResponseEntity.ok(noDataResponse);
        }
        
        return ResponseEntity.ok(plans);
    }

    /**
     * Subscribe to a membership plan
     */
    @PostMapping(ApiEndpoints.Membership.SUBSCRIBE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDTO> subscribeToPlan(
            @Valid @RequestBody SubscriptionRequestDTO request) {
        
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} subscribing to plan {}", userId, request.getPlanId());
        
        SubscriptionResponseDTO subscription = membershipService.subscribeToPlan(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    /**
     * Get current subscription for authenticated user
     */
    @GetMapping(ApiEndpoints.Membership.SUBSCRIPTION_CURRENT)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCurrentSubscription() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("Fetching current subscription for user: {}", userId);
        
        SubscriptionResponseDTO subscription = membershipService.getCurrentSubscription(userId);
        if (subscription == null) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No active subscription found",
                "User does not have an active subscription",
                "/api/v1/membership/subscription/current"
            );
            return ResponseEntity.ok(noDataResponse);
        }
        return ResponseEntity.ok(subscription);
    }

    /**
     * Get subscription history for authenticated user
     */
    @GetMapping(ApiEndpoints.Membership.SUBSCRIPTION_HISTORY)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserSubscriptionHistory() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("Fetching subscription history for user: {}", userId);
        
        List<SubscriptionHistoryResponseDTO> history = membershipService.getUserSubscriptionHistory(userId);
        
        if (history.isEmpty()) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No subscription history found",
                "User has no subscription history",
                "/api/v1/membership/subscription/history"
            );
            return ResponseEntity.ok(noDataResponse);
        }
        
        return ResponseEntity.ok(history);
    }

    /**
     * Cancel current subscription
     */
    @PostMapping(ApiEndpoints.Membership.SUBSCRIPTION_CANCEL)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDTO> cancelSubscription(
            @RequestParam String reason) {
        
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} cancelling subscription with reason: {}", userId, reason);
        
        SubscriptionResponseDTO subscription = membershipService.cancelSubscription(userId, reason);
        return ResponseEntity.ok(subscription);
    }

    /**
     * Upgrade membership tier
     */
    @PostMapping(ApiEndpoints.Membership.TIER_UPGRADE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDTO> upgradeTier(
            @PathVariable Long tierId) {
        
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} upgrading to tier: {}", userId, tierId);
        
        SubscriptionResponseDTO subscription = membershipService.upgradeTier(userId, tierId, false);
        return ResponseEntity.ok(subscription);
    }

    /**
     * Downgrade membership tier
     */
    @PostMapping(ApiEndpoints.Membership.TIER_DOWNGRADE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDTO> downgradeTier(
            @PathVariable Long tierId) {
        
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} downgrading to tier: {}", userId, tierId);
        
        SubscriptionResponseDTO subscription = membershipService.downgradeTier(userId, tierId, false);
        return ResponseEntity.ok(subscription);
    }

    /**
     * Renew subscription
     */
    @PostMapping(ApiEndpoints.Membership.SUBSCRIPTION_RENEW)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponseDTO> renewSubscription() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} renewing subscription", userId);
        
        SubscriptionResponseDTO subscription = membershipService.renewSubscription(userId);
        return ResponseEntity.ok(subscription);
    }

    /**
     * Get user's membership status
     */
    @GetMapping(ApiEndpoints.Membership.STATUS)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserMembershipStatus() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("Fetching membership status for user: {}", userId);
        
        SubscriptionResponseDTO status = membershipService.getUserMembershipStatus(userId);
        if (status == null) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No membership status found",
                "User does not have an active membership",
                "/api/v1/membership/status"
            );
            return ResponseEntity.ok(noDataResponse);
        }
        return ResponseEntity.ok(status);
    }
}
