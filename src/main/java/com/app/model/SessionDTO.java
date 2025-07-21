package com.app.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {
    private String integrationId; // maps to userId
    private List<ItemDTO> cartItems;
    private double cartTotal;

    public static SessionDTO fromCartRequest(CartRequest req) {
        return SessionDTO.builder()
                .integrationId(req.getUserId().toString())
                .cartItems(req.getItems())
                .cartTotal(req.getTotal())
                .build();
    }
}
