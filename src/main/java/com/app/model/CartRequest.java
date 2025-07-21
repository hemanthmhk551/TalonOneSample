package com.app.model;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest {
    @NotNull
    private Long userId;

    @NotEmpty
    private List<ItemDTO> items;

    @NotNull
    private Double total;
}
