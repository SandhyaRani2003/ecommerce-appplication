package com.example.ecommerce.service;

import com.example.ecommerce.dto.cart.AddCartItemRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        // Create test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);

        // Create test cart item
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);

        // Create test cart
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>(List.of(testCartItem)));
    }

    @Test
    void testGetMyCart_Success() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

            // Act
            CartResponse result = cartService.getMyCart();

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getCartId());
            assertEquals(1, result.getItems().size());
            assertEquals(100.0, result.getTotalAmount());
        }
    }

    @Test
    void testGetMyCart_CartNotFound() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                cartService.getMyCart();
            });
        }
    }

    @Test
    void testAddItem_Success() {
        // Arrange
        AddCartItemRequest request = new AddCartItemRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
            when(productService.findEntityById(1L)).thenReturn(testProduct);
            when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
            when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

            // Act
            CartResponse result = cartService.addItem(request);

            // Assert
            assertNotNull(result);
            verify(cartItemRepository, times(1)).save(any(CartItem.class));
        }
    }

    @Test
    void testAddItem_ProductNotFound() {
        // Arrange
        AddCartItemRequest request = new AddCartItemRequest();
        request.setProductId(999L);
        request.setQuantity(1);

        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
            when(productService.findEntityById(999L))
                    .thenThrow(new ResourceNotFoundException("Product not found"));

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                cartService.addItem(request);
            });
        }
    }

    @Test
    void testRemoveItem_Success() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));


            // Act
            CartResponse result = cartService.removeItem(1L);

            // Assert
            assertNotNull(result);
            verify(cartItemRepository, times(1)).delete(any(CartItem.class));
        }
    }

    @Test
    void testUpdateItem_Success() {
        // Arrange
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(5);

        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
            when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

            // Act
            CartResponse result = cartService.updateItem(1L, request);

            // Assert
            assertNotNull(result);
            verify(cartItemRepository, times(1)).save(any(CartItem.class));
        }
    }

    @Test
    void testRemoveItem_ItemNotFound() {
        // Arrange
        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        emptyCart.setUser(testUser);
        emptyCart.setItems(new ArrayList<>());

        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(emptyCart));

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                cartService.removeItem(999L);
            });
        }
    }
}

