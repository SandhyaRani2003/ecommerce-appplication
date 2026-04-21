package com.example.ecommerce.dto.order;

import com.example.ecommerce.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private double totalAmount;
    private Instant createdAt;
    private List<OrderItemResponse> items;
}

