package com.example.ecommerce.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponse {
    private Long itemId;
    private Long productId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double lineTotal;
}

