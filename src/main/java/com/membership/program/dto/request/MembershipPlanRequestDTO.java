package com.membership.program.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlanRequestDTO {

    @NotBlank(message = "Plan name is required")
    private String name;

    private String description;

    @NotNull(message = "Duration in months is required")
    @Positive(message = "Duration must be positive")
    private Integer durationMonths;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal discountPercentage;

    private Integer maxTierLevel;

    private boolean active = true;
}
