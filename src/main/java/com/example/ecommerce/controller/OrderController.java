package com.example.ecommerce.controller;

import com.example.ecommerce.dto.order.OrderResponse;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout() {
        return orderService.checkout();
    }

    @GetMapping
    public List<OrderResponse> myOrders() {
        return orderService.myOrders();
    }
}

