package com.app.model;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsUpdateRequest {
    @NotNull
    @Min(0)
    private Integer totalOrders;

    @NotNull
    @Min(0)
    private Double totalSpent;
}
