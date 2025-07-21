package com.app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Order order;
    private RewardsResponse rewards;

    public OrderResponse(Order order, RewardsResponse rewards) {
        this.order = order;
        this.rewards = rewards;
    }
}
