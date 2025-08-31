package com.membership.program.repository;

import com.membership.program.entity.Subscription;
import com.membership.program.dto.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {


    /**
     * Find current active subscription for a user
     */
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.expiryDate > :now ORDER BY s.expiryDate DESC")
    Optional<Subscription> findCurrentActiveSubscription(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Find all expired subscriptions that need processing
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.expiryDate <= :now")
    List<Subscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    /**
     * Find all subscriptions expiring within the next specified days
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.expiryDate BETWEEN :now AND :futureDate")
    List<Subscription> findSubscriptionsExpiringSoon(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

}
