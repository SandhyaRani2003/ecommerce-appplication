package com.example.ecommerce.service;

import com.example.ecommerce.dto.cart.AddCartItemRequest;
import com.example.ecommerce.dto.cart.CartItemResponse;
import com.example.ecommerce.dto.cart.CartResponse;
import com.example.ecommerce.dto.cart.UpdateCartItemRequest;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        Cart cart = getCurrentUserCart();
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        Cart cart = getCurrentUserCart();
        Product product = productService.findEntityById(request.getProductId());

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> CartItem.builder().cart(cart).product(product).quantity(0).build());

        item.setQuantity(item.getQuantity() + request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(getCurrentUserCart());
    }

    @Transactional
    public CartResponse updateItem(Long itemId, UpdateCartItemRequest request) {
        Cart cart = getCurrentUserCart();

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(getCurrentUserCart());
    }

    @Transactional
    public CartResponse removeItem(Long itemId) {
        Cart cart = getCurrentUserCart();

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);
        return toResponse(getCurrentUserCart());
    }

    @Transactional
    public void clearCurrentCart() {
        Cart cart = getCurrentUserCart();
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Cart getCurrentUserCart() {
        User user = getCurrentUser();
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> {
                    double lineTotal = item.getProduct().getPrice() * item.getQuantity();
                    return new CartItemResponse(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getPrice(),
                            item.getQuantity(),
                            lineTotal);
                })
                .toList();

        double total = items.stream().mapToDouble(CartItemResponse::getLineTotal).sum();

        return new CartResponse(cart.getId(), cart.getUser().getId(), items, total);
    }
}

