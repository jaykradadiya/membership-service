package com.membership.program.repository;

import com.membership.program.entity.Order;
import com.membership.program.dto.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by user ID and status
     */
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

}
