package com.app.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardsResponse {
    private double totalDiscount;
    private List<DiscountDTO> discounts;
    private boolean loyaltyUsed;
    private int loyaltyPointsUsed;
    private int loyaltyPointsRemaining;
}
