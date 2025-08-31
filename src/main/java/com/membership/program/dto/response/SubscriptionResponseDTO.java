package com.membership.program.dto.response;

import com.membership.program.dto.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long planId;
    private String planName;
    private Long tierId;
    private String tierName;
    private Integer tierLevel;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private BigDecimal actualPrice;
    private BigDecimal discountedPrice;
    private BigDecimal effectivePrice;
    private boolean autoRenewal;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
