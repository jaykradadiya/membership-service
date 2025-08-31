package com.membership.program.service;

import com.membership.program.dto.enums.SubscriptionAction;
import com.membership.program.entity.Subscription;
import com.membership.program.entity.SubscriptionHistory;
import com.membership.program.entity.User;
import com.membership.program.repository.SubscriptionHistoryRepository;
import com.membership.program.repository.SubscriptionRepository;
import com.membership.program.repository.UserRepository;
import com.membership.program.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierEvaluationScheduler {

    private final TierUpgradeService tierUpgradeService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final MembershipService membershipService;

    /**
     * Scheduled task to evaluate tier upgrades for all users daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void evaluateTierUpgradesForAllUsers() {
        log.info("Starting scheduled tier evaluation for all users");
        
        try {
            List<User> users = userRepository.findAll();
            int processedCount = 0;
            int upgradedCount = 0;

            for (User user : users) {
                try {
                    // Check if user needs tier evaluation (evaluation frequency check)
                    if (shouldEvaluateUser(user)) {
                        // Use the new tier upgrade service to process automatic upgrades
                        tierUpgradeService.processAutomaticUpgrades(user.getId());
                        processedCount++;
                        
                        // Check if tier was actually upgraded
                        if (userRepository.findById(user.getId())
                                .map(u -> !u.getCurrentTierLevel().equals(user.getCurrentTierLevel()))
                                .orElse(false)) {
                            upgradedCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Error evaluating tier upgrade for user {}: {}", user.getId(), e.getMessage());
                }
            }

            log.info("Tier evaluation completed. Processed: {}, Upgraded: {}", processedCount, upgradedCount);
        } catch (Exception e) {
            log.error("Error in scheduled tier evaluation: {}", e.getMessage());
        }
    }

    /**
     * Scheduled task to process expired subscriptions daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void processExpiredSubscriptions() {
        log.info("Starting scheduled processing of expired subscriptions");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(now);
            
            if (expiredSubscriptions.isEmpty()) {
                log.info("No expired subscriptions found");
                return;
            }
            
            log.info("Found {} expired subscriptions to process", expiredSubscriptions.size());
            
            int renewedCount = 0;
            int markedInactiveCount = 0;
            
            for (Subscription subscription : expiredSubscriptions) {
                try {
                    if (subscription.isAutoRenewal()) {
                        // Auto-renew the subscription
                        renewSubscription(subscription);
                        renewedCount++;
                        log.info("Auto-renewed subscription {} for user {}", subscription.getId(), subscription.getUser().getId());
                    } else {
                        // Mark subscription as expired
                        markSubscriptionAsExpired(subscription);
                        markedInactiveCount++;
                        log.info("Marked subscription {} as expired for user {}", subscription.getId(), subscription.getUser().getId());
                    }
                } catch (Exception e) {
                    log.error("Error processing expired subscription {}: {}", subscription.getId(), e.getMessage());
                }
            }
            
            log.info("Expired subscription processing completed. Renewed: {}, Marked inactive: {}", 
                    renewedCount, markedInactiveCount);
                    
        } catch (Exception e) {
            log.error("Error in scheduled expired subscription processing: {}", e.getMessage());
        }
    }


    /**
     * Check if a user should be evaluated for tier upgrade
     */
    private boolean shouldEvaluateUser(User user) {
        // Skip users without membership start date
        if (user.getMembershipStartDate() == null) {
            return false;
        }

        // Check if enough time has passed since last evaluation
        if (user.getLastTierEvaluationDate() != null) {
            long daysSinceLastEvaluation = java.time.temporal.ChronoUnit.DAYS.between(
                    user.getLastTierEvaluationDate(), LocalDateTime.now());
            
            // Evaluate every 30 days
            return daysSinceLastEvaluation >= 30;
        }

        // If never evaluated, check if user has been a member for at least 30 days
        long daysSinceMembershipStart = java.time.temporal.ChronoUnit.DAYS.between(
                user.getMembershipStartDate(), LocalDateTime.now());
        
        return daysSinceMembershipStart >= 30;
    }

    /**
     * Renew a subscription by extending its expiry date
     */
    private void renewSubscription(Subscription subscription) {
        try {
            // Calculate new expiry date based on plan duration
            LocalDateTime newExpiryDate = calculateNewExpiryDate(subscription);
            
            // Renew the subscription
            subscription.renew(newExpiryDate);
            
            // Save the updated subscription
            subscriptionRepository.save(subscription);
            // Create subscription history
            createSubscriptionHistory(subscription, SubscriptionAction.RENEWED, "Subscription renewed", subscription.getPlan().getName(), subscription.getPlan().getName(), subscription.getPlan().getPrice(), subscription.getPlan().getPrice(), "SYSTEM");


            log.info("Subscription {} renewed successfully until {}", subscription.getId(), newExpiryDate);
            
        } catch (Exception e) {
            log.error("Error renewing subscription {}: {}", subscription.getId(), e.getMessage());
            throw new RuntimeException("Failed to renew subscription", e);
        }
    }

    /**
     * Mark a subscription as expired
     */
    private void markSubscriptionAsExpired(Subscription subscription) {
        try {
            // Update subscription status to EXPIRED
            subscription.setStatus(com.membership.program.dto.enums.SubscriptionStatus.EXPIRED);
            
            // Save the updated subscription
            subscriptionRepository.save(subscription);

            createSubscriptionHistory(subscription, SubscriptionAction.CANCELLED, "Subscription Cancelled", subscription.getPlan().getName(), null, subscription.getPlan().getPrice(), null, "SYSTEM");

            
            log.info("Subscription {} marked as expired", subscription.getId());
            
        } catch (Exception e) {
            log.error("Error marking subscription {} as expired: {}", subscription.getId(), e.getMessage());
            throw new RuntimeException("Failed to mark subscription as expired", e);
        }
    }

    /**
     * Calculate new expiry date based on subscription plan duration
     */
    private LocalDateTime calculateNewExpiryDate(Subscription subscription) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get the plan duration in months
        int durationMonths = subscription.getPlan().getDurationMonths();
        
        // Calculate new expiry date
        LocalDateTime newExpiryDate = now.plusMonths(durationMonths);
        
        // If the calculated date is before the current expiry date, 
        // extend from the current expiry date instead
        if (newExpiryDate.isBefore(subscription.getExpiryDate())) {
            newExpiryDate = subscription.getExpiryDate().plusMonths(durationMonths);
        }
        
        return newExpiryDate;
    }

    private void createSubscriptionHistory(Subscription subscription, SubscriptionAction action,
                                           String description, String oldValue, String newValue, BigDecimal oldPrice, BigDecimal newPrice, String performedBy) {
        SubscriptionHistory history = SubscriptionHistory.builder()
                .subscription(subscription)
                .action(action)
                .actionDescription(description)
                .oldValue(oldValue)
                .newValue(newValue)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .performedBy(performedBy)
                .performedAt(LocalDateTime.now())
                .build();

        subscriptionHistoryRepository.save(history);
    }

}
