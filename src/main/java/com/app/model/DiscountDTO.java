package com.app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDTO {
    private String code;
    private String description;
    private double amount;
}
