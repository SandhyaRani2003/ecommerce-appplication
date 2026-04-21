package com.example.ecommerce.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double lineTotal;
}

