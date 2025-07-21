package com.app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private int totalOrders;
    private double totalSpent;
    private int loyaltyPoints;

    public static UserDTO fromEntity(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .totalOrders(user.getTotalOrders())
                .totalSpent(user.getTotalSpent())
                .loyaltyPoints(user.getLoyaltyPoints())
                .build();
    }
}
