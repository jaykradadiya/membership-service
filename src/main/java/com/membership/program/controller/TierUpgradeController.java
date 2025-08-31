package com.membership.program.controller;

import com.membership.program.constants.ApiEndpoints;
import com.membership.program.dto.evaluation.EvaluationResult;
import com.membership.program.dto.evaluation.TierUpgradeRuleDefinition;
import com.membership.program.dto.response.NoDataResponse;
import com.membership.program.service.TierUpgradeService;
import com.membership.program.utility.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.TierUpgrade.BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class TierUpgradeController {

    private final TierUpgradeService tierUpgradeService;
    private final SecurityContextUtil securityContextUtil;

    /**
     * Evaluate tier upgrade eligibility for current user
     */
    @GetMapping(ApiEndpoints.TierUpgrade.EVALUATE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> evaluateTierUpgrade() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} evaluating tier upgrade eligibility", userId);
        
        List<EvaluationResult> results = tierUpgradeService.evaluateTierUpgrade(userId);
        
        if (results.isEmpty()) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No tier upgrade evaluation results found",
                "User is not eligible for any tier upgrades",
                "/api/v1/tier-upgrade/evaluate"
            );
            return ResponseEntity.ok(noDataResponse);
        }
        
        return ResponseEntity.ok(results);
    }

    /**
     * Get detailed evaluation results for current user
     */
    @GetMapping(ApiEndpoints.TierUpgrade.EVALUATE_DETAILED)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDetailedEvaluationResults() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} getting detailed evaluation results", userId);
        
        List<EvaluationResult> results = tierUpgradeService.getDetailedEvaluationResults(userId);
        
        if (results.isEmpty()) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No detailed evaluation results found",
                "User is not eligible for any tier upgrades",
                "/api/v1/tier-upgrade/evaluate/detailed"
            );
            return ResponseEntity.ok(noDataResponse);
        }
        
        return ResponseEntity.ok(results);
    }

    /**
     * Check if current user is eligible for tier upgrade
     */
    @GetMapping(ApiEndpoints.TierUpgrade.ELIGIBILITY)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> checkEligibility() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} checking tier upgrade eligibility", userId);
        
        boolean eligible = tierUpgradeService.isUserEligibleForUpgrade(userId);
        return ResponseEntity.ok(eligible);
    }

    /**
     * Get best applicable upgrade rule for current user
     */
    @GetMapping(ApiEndpoints.TierUpgrade.BEST_RULE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getBestApplicableRule() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} getting best applicable upgrade rule", userId);
        
        TierUpgradeRuleDefinition rule = tierUpgradeService.getBestApplicableRule(userId);
        if (rule == null) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No applicable upgrade rule found",
                "User does not meet criteria for any tier upgrades",
                "/api/v1/tier-upgrade/best-rule"
            );
            return ResponseEntity.ok(noDataResponse);
        }
        return ResponseEntity.ok(rule);
    }

    /**
     * Get all applicable rules for current user
     */
    @GetMapping(ApiEndpoints.TierUpgrade.APPLICABLE_RULES)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getApplicableRules() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} getting applicable upgrade rules", userId);
        
        List<TierUpgradeRuleDefinition> rules = tierUpgradeService.getApplicableRules(userId);
        
        if (rules.isEmpty()) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No applicable upgrade rules found",
                "User does not meet criteria for any tier upgrades",
                "/api/v1/tier-upgrade/applicable-rules"
            );
            return ResponseEntity.ok(noDataResponse);
        }
        
        return ResponseEntity.ok(rules);
    }

    /**
     * Process automatic tier upgrades for current user
     */
    @PostMapping(ApiEndpoints.TierUpgrade.PROCESS_AUTO)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> processAutomaticUpgrades() {
        Long userId = securityContextUtil.getCurrentUserId();
        log.info("User {} processing automatic tier upgrades", userId);
        
        tierUpgradeService.processAutomaticUpgrades(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Admin endpoint: Evaluate tier upgrade for any user
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(ApiEndpoints.TierUpgrade.ADMIN_EVALUATE)
    public ResponseEntity<?> evaluateTierUpgradeForUser(@PathVariable Long userId) {
        log.info("Admin evaluating tier upgrade for user: {}", userId);
        
        List<EvaluationResult> results = tierUpgradeService.evaluateTierUpgrade(userId);
        
        if (results.isEmpty()) {
            NoDataResponse noDataResponse = NoDataResponse.create(
                "No tier upgrade evaluation results found for user " + userId,
                "User is not eligible for any tier upgrades",
                "/api/v1/tier-upgrade/admin/evaluate/" + userId
            );
            return ResponseEntity.ok(noDataResponse);
        }
        
        return ResponseEntity.ok(results);
    }

    /**
     * Admin endpoint: Process automatic upgrades for any user
     */
    @PostMapping(ApiEndpoints.TierUpgrade.ADMIN_PROCESS_AUTO)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processAutomaticUpgradesForUser(@PathVariable Long userId) {
        log.info("Admin processing automatic tier upgrades for user: {}", userId);
        
        tierUpgradeService.processAutomaticUpgrades(userId);
        return ResponseEntity.ok().build();
    }
}
