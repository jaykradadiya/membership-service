package com.membership.program.dto.response;

import com.membership.program.dto.enums.SubscriptionAction;

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
public class SubscriptionHistoryResponseDTO {

    private Long id;
    private SubscriptionAction action;
    private String actionDescription;
    private String oldValue;
    private String newValue;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private LocalDateTime performedAt;
    private String performedBy;
    private String metadata;
}
