package com.membership.program.dto.response;

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
public class MembershipPlanResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Integer durationMonths;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountPercentage;
    private Integer maxTierLevel;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
