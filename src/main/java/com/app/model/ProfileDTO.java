package com.app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private String integrationId; // maps to userId
    private String email;
    private String name;
    private int totalOrders;
    private double totalSpent;
    private int loyaltyPoints;

    public static ProfileDTO fromCartRequest(CartRequest req) {
        return ProfileDTO.builder()
                .integrationId(req.getUserId().toString())
                .build();
    }
}
