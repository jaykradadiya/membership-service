package com.membership.program.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestDTO {

    @NotNull(message = "Plan ID is required")
    @Positive(message = "Plan ID must be positive")
    private Long planId;

    @NotNull(message = "Tier ID is required")
    @Positive(message = "Tier ID must be positive")
    private Long tierId;

    private boolean autoRenewal = true;
}
