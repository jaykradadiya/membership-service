package com.membership.program.repository;

import com.membership.program.entity.SubscriptionHistory;
import com.membership.program.dto.enums.SubscriptionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {


    /**
     * Find history by user ID (through subscription)
     */
    @Query("SELECT sh FROM SubscriptionHistory sh WHERE sh.subscription.user.id = :userId " +
           "ORDER BY sh.performedAt DESC")
    List<SubscriptionHistory> findByUserId(@Param("userId") Long userId);

}
