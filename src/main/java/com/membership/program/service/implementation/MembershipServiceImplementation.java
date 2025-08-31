package com.membership.program.service.implementation;

import com.membership.program.dto.enums.OrderStatus;
import com.membership.program.dto.request.SubscriptionRequestDTO;
import com.membership.program.dto.response.MembershipPlanResponseDTO;
import com.membership.program.dto.response.SubscriptionHistoryResponseDTO;
import com.membership.program.dto.response.SubscriptionResponseDTO;
import com.membership.program.entity.*;
import com.membership.program.dto.enums.SubscriptionAction;
import com.membership.program.dto.enums.SubscriptionStatus;
import com.membership.program.exception.MembershipException;
import com.membership.program.exception.SubscriptionException;
import com.membership.program.repository.*;
import com.membership.program.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipServiceImplementation implements MembershipService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlanResponseDTO> getMembershipPlans(Boolean includeDiscountsOnly) {
        if (includeDiscountsOnly != null && includeDiscountsOnly) {
            log.info("Fetching membership plans with discounts");
            List<MembershipPlan> plans = membershipPlanRepository.findPlansWithDiscounts();
            return plans.stream()
                    .map(this::mapToPlanResponseDTO)
                    .collect(Collectors.toList());
        } else {
            log.info("Fetching all active membership plans");
            List<MembershipPlan> plans = membershipPlanRepository.findByActiveTrue();
            return plans.stream()
                    .map(this::mapToPlanResponseDTO)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlanResponseDTO> getMembershipPlansForTier(Integer tierLevel) {
        log.info("Fetching membership plans for tier level: {}", tierLevel);
        List<MembershipPlan> plans = membershipPlanRepository.findApplicablePlansForTier(tierLevel);
        return plans.stream()
                .map(this::mapToPlanResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponseDTO subscribeToPlan(Long userId, SubscriptionRequestDTO request) {
        log.info("User {} subscribing to plan {} with tier {}", userId, request.getPlanId(), request.getTierId());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MembershipException("User not found with ID: " + userId));
        
        MembershipPlan plan = membershipPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new MembershipException("Membership plan not found with ID: " + request.getPlanId()));
        
        MembershipTier tier = membershipTierRepository.findById(request.getTierId())
                .orElseThrow(() -> new MembershipException("Membership tier not found with ID: " + request.getTierId()));

        // Validate plan and tier compatibility
        if (!plan.isApplicableForTier(tier.getTierLevel())) {
            throw new MembershipException("Plan " + plan.getName() + " is not applicable for tier " + tier.getName());
        }

        // Check if user already has an active subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findCurrentActiveSubscription(userId, LocalDateTime.now());
        if (existingSubscription.isPresent()) {
            throw new SubscriptionException("User already has an active subscription");
        }

        // Create new subscription
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime expiryDate = startDate.plusMonths(plan.getDurationMonths());
        
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .expiryDate(expiryDate)
                .actualPrice(plan.getPrice())
                .discountedPrice(plan.getDiscountedPrice())
                .autoRenewal(request.isAutoRenewal())
                .build();

        subscription = subscriptionRepository.save(subscription);
        
        // Update user's tier level and membership start date
        user.setCurrentTierLevel(tier.getTierLevel());
        if (user.getMembershipStartDate() == null) {
            user.setMembershipStartDate(startDate);
        }
        userRepository.save(user);

        // Create subscription history
        createSubscriptionHistory(subscription, SubscriptionAction.CREATED, "Subscription created", null, plan.getName(),null, plan.getPrice(), user.getUsername());

        log.info("User {} successfully subscribed to plan {} with tier {}", userId, plan.getName(), tier.getName());
        return mapToSubscriptionResponseDTO(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getCurrentSubscription(Long userId) {
        log.info("Fetching current subscription for user: {}", userId);
        Optional<Subscription> subscription = subscriptionRepository.findCurrentActiveSubscription(userId, LocalDateTime.now());
        return subscription.map(this::mapToSubscriptionResponseDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionHistoryResponseDTO> getUserSubscriptionHistory(Long userId) {
        log.info("Fetching subscription history for user: {}", userId);
        List<SubscriptionHistory> subscriptions = subscriptionHistoryRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(this::mapToSubscriptionHistoryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponseDTO cancelSubscription(Long userId, String reason) {
        log.info("Cancelling subscription for user: {} with reason: {}", userId, reason);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MembershipException("User not found with ID: " + userId));
        
        Subscription subscription = subscriptionRepository.findCurrentActiveSubscription(userId, LocalDateTime.now())
                .orElseThrow(() -> new SubscriptionException("No active subscription found for user: " + userId));
        
        MembershipPlan plan = subscription.getPlan();
        subscription.cancel(reason, user.getUsername());
        subscription = subscriptionRepository.save(subscription);

        // Create subscription history
        createSubscriptionHistory(subscription, SubscriptionAction.CANCELLED, "Subscription cancelled: " + reason, plan.getName(), null, plan.getPrice(), null, user.getUsername());

        log.info("Subscription cancelled successfully for user: {}", userId);
        return mapToSubscriptionResponseDTO(subscription);
    }

    @Override
    public SubscriptionResponseDTO upgradeTier(Long userId, Long newTierId,boolean isAutoUpgrade) {
        log.info("Upgrading tier for user: {} to tier: {}", userId, newTierId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MembershipException("User not found with ID: " + userId));
        
        MembershipTier newTier = membershipTierRepository.findById(newTierId)
                .orElseThrow(() -> new MembershipException("Membership tier not found with ID: " + newTierId));

        if (newTier.getTierLevel() <= user.getCurrentTierLevel()) {
            throw new MembershipException("New tier level must be higher than current tier level");
        }

        // Update user's tier level
        Integer oldTierLevel = user.getCurrentTierLevel();
        user.setCurrentTierLevel(newTier.getTierLevel());
        userRepository.save(user);

        // Update current subscription if exists
        Optional<Subscription> currentSubscription = subscriptionRepository.findCurrentActiveSubscription(userId, LocalDateTime.now());
        if (currentSubscription.isPresent()) {
            Subscription subscription = currentSubscription.get();
            MembershipTier oldTier = subscription.getTier();
            subscription.setTier(newTier);
            subscription = subscriptionRepository.save(subscription);
            String membershipUpdatePerformedBy = isAutoUpgrade ? "SYSTEM" : user.getUsername();
            // Create subscription history
            createSubscriptionHistory(subscription, SubscriptionAction.UPGRADED,
                    "Tier upgraded from " + oldTier.getName() + " to " + newTier.getName(),
                    oldTier.getName(), newTier.getName(), oldTier.getDiscountPercentage(), newTier.getDiscountPercentage(), membershipUpdatePerformedBy);
        }

        log.info("User {} tier upgraded from {} to {}", userId, oldTierLevel, newTier.getTierLevel());
        return getCurrentSubscription(userId);
    }

    @Override
    public SubscriptionResponseDTO downgradeTier(Long userId, Long newTierId,boolean isAutoDowngrade) {
        log.info("Downgrading tier for user: {} to tier: {}", userId, newTierId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MembershipException("User not found with ID: " + userId));
        
        MembershipTier newTier = membershipTierRepository.findById(newTierId)
                .orElseThrow(() -> new MembershipException("Membership tier not found with ID: " + newTierId));

        if (newTier.getTierLevel() >= user.getCurrentTierLevel()) {
            throw new MembershipException("New tier level must be lower than current tier level");
        }

        // Update user's tier level
        Integer oldTierLevel = user.getCurrentTierLevel();
        user.setCurrentTierLevel(newTier.getTierLevel());
        userRepository.save(user);

        // Update current subscription if exists
        Optional<Subscription> currentSubscription = subscriptionRepository.findCurrentActiveSubscription(userId, LocalDateTime.now());
        if (currentSubscription.isPresent()) {
            Subscription subscription = currentSubscription.get();
            MembershipTier oldTier = subscription.getTier();
            subscription.setTier(newTier);
            subscription = subscriptionRepository.save(subscription);

            String membershipUpdatePerformedBy = isAutoDowngrade ? "SYSTEM" : user.getUsername();

            // Create subscription history
            createSubscriptionHistory(subscription, SubscriptionAction.DOWNGRADED,
                    "Tier downgraded from " + oldTier.getName() + " to " + newTier.getName(),
                        oldTier.getName(), newTier.getName(), oldTier.getDiscountPercentage(), newTier.getDiscountPercentage(), membershipUpdatePerformedBy);
        }

        log.info("User {} tier downgraded from {} to {}", userId, oldTierLevel, newTier.getTierLevel());
        return getCurrentSubscription(userId);
    }

    @Override
    public SubscriptionResponseDTO renewSubscription(Long userId) {
        log.info("Renewing subscription for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MembershipException("User not found with ID: " + userId));

        Subscription subscription = subscriptionRepository.findCurrentActiveSubscription(userId, LocalDateTime.now())
                .orElseThrow(() -> new SubscriptionException("No active subscription found for user: " + userId));

        LocalDateTime newExpiryDate = subscription.getExpiryDate().plusMonths(subscription.getPlan().getDurationMonths());
        subscription.renew(newExpiryDate);
        subscription = subscriptionRepository.save(subscription);

        // Create subscription history
        createSubscriptionHistory(subscription, SubscriptionAction.RENEWED, "Subscription renewed", subscription.getPlan().getName(), subscription.getPlan().getName(), subscription.getPlan().getPrice(), subscription.getPlan().getPrice(), user.getUsername());

        log.info("Subscription renewed successfully for user: {}", userId);
        return mapToSubscriptionResponseDTO(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getUserMembershipStatus(Long userId) {
        log.info("Fetching membership status for user: {}", userId);
        return getCurrentSubscription(userId);
    }



    // Private helper methods
    private MembershipPlanResponseDTO mapToPlanResponseDTO(MembershipPlan plan) {
        return MembershipPlanResponseDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .durationMonths(plan.getDurationMonths())
                .price(plan.getPrice())
                .discountedPrice(plan.getDiscountedPrice())
                .discountPercentage(plan.getDiscountPercentage())
                .maxTierLevel(plan.getMaxTierLevel())
                .active(plan.isActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    private SubscriptionResponseDTO mapToSubscriptionResponseDTO(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .username(subscription.getUser().getUsername())
                .planId(subscription.getPlan().getId())
                .planName(subscription.getPlan().getName())
                .tierId(subscription.getTier().getId())
                .tierName(subscription.getTier().getName())
                .tierLevel(subscription.getTier().getTierLevel())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .expiryDate(subscription.getExpiryDate())
                .actualPrice(subscription.getActualPrice())
                .discountedPrice(subscription.getDiscountedPrice())
                .effectivePrice(subscription.getEffectivePrice())
                .autoRenewal(subscription.isAutoRenewal())
                .cancellationReason(subscription.getCancellationReason())
                .cancelledAt(subscription.getCancelledAt())
                .cancelledBy(subscription.getCancelledBy())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
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

    private SubscriptionHistoryResponseDTO mapToSubscriptionHistoryResponseDTO(SubscriptionHistory history) {
        return SubscriptionHistoryResponseDTO.builder()
                .id(history.getId())
                .action(history.getAction())
                .actionDescription(history.getActionDescription())
                .oldValue(history.getOldValue())
                .newValue(history.getNewValue())
                .oldPrice(history.getOldPrice())
                .newPrice(history.getNewPrice())
                .performedAt(history.getPerformedAt())
                .performedBy(history.getPerformedBy())
                .metadata(history.getMetadata())
                .build();
    }
}
