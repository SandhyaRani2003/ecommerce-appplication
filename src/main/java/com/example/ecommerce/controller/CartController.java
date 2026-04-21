package com.example.ecommerce.controller;

import com.example.ecommerce.dto.cart.AddCartItemRequest;
import com.example.ecommerce.dto.cart.CartResponse;
import com.example.ecommerce.dto.cart.UpdateCartItemRequest;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getMyCart() {
        return cartService.getMyCart();
    }

    @PostMapping("/items")
    public CartResponse addItem(@Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(@PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@PathVariable Long itemId) {
        return cartService.removeItem(itemId);
    }
}

