package com.membership.program.service.evaluation;

import com.membership.program.dto.evaluation.EvaluationContext;
import com.membership.program.dto.enums.OrderStatus;
import com.membership.program.entity.Order;
import com.membership.program.entity.User;
import com.membership.program.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simplified service for building evaluation contexts from user data
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationContextBuilder {
    
    private final OrderRepository orderRepository;
    
    /**
     * Build evaluation context for a user with only required metrics
     */
    public EvaluationContext buildContext(User user) {
        log.debug("Building simplified evaluation context for user: {}", user.getId());
        
        // Get user's completed orders
        List<Order> completedOrders = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.COMPLETED);
        
        // Calculate total order count
        int totalOrderCount = completedOrders.size();
        
        // Calculate monthly order value
        BigDecimal monthlyOrderValue = calculateMonthlyOrderValue(completedOrders);
        
        return EvaluationContext.builder()
                .userId(user.getId())
                .user(user)
                .totalOrderCount(totalOrderCount)
                .monthlyOrderValue(monthlyOrderValue)
                .userCohort(user.getCohort())
                .build();
    }
    
    private BigDecimal calculateMonthlyOrderValue(List<Order> orders) {
        LocalDateTime now = LocalDateTime.now();
        
        return orders.stream()
                .filter(order -> isInCurrentMonth(order.getCreatedAt(), now))
                .map(Order::getEffectiveAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private boolean isInCurrentMonth(LocalDateTime date, LocalDateTime now) {
        return date.getYear() == now.getYear() && date.getMonth() == now.getMonth();
    }
}
