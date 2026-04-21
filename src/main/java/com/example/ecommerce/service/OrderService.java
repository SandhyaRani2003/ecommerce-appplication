package com.example.ecommerce.service;

import com.example.ecommerce.dto.order.OrderItemResponse;
import com.example.ecommerce.dto.order.OrderResponse;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public OrderResponse checkout() {
        Cart cart = cartService.getCurrentUserCart();

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot checkout with an empty cart");
        }

        User user = getCurrentUser();

        CustomerOrder order = CustomerOrder.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .totalAmount(0.0)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getProduct().getPrice())
                        .build())
                .toList();

        order.setItems(orderItems);

        double total = orderItems.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(total);

        CustomerOrder saved = orderRepository.save(order);
        cartService.clearCurrentCart();

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private OrderResponse toResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getUnitPrice() * item.getQuantity()
                ))
                .toList();

        return new OrderResponse(order.getId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), items);
    }
}

