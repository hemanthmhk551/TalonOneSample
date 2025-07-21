package com.app.model;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    @NotNull
    private Long userId;

    @NotEmpty
    private List<ItemDTO> items;

    @NotNull
    private Double total;

    /**
     * Converts this OrderRequest to a CartRequest for rewards evaluation.
     */
    public CartRequest toCartRequest() {
        return CartRequest.builder()
                .userId(userId)
                .items(items)
                .total(total)
                .build();
    }
}
