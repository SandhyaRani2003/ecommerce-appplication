package com.example.ecommerce.service;

import com.example.ecommerce.dto.order.OrderResponse;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.OrderRepository;
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

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;
    private CartItem cartItem;

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
        testProduct.setPrice(1000.0);

        // Create cart item
        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);

        // Create test cart
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>(List.of(cartItem)));
    }

    @Test
    void testCheckout_Success() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(cartService.getCurrentUserCart()).thenReturn(testCart);
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            CustomerOrder savedOrder = new CustomerOrder();
            savedOrder.setId(1L);
            savedOrder.setUser(testUser);
            savedOrder.setStatus(OrderStatus.CREATED);
            savedOrder.setTotalAmount(2000.0);
            savedOrder.setItems(new ArrayList<>());

            when(orderRepository.save(any(CustomerOrder.class))).thenReturn(savedOrder);

            // Act
            OrderResponse result = orderService.checkout();

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getOrderId());
            assertEquals(OrderStatus.CREATED, result.getStatus());
            verify(orderRepository, times(1)).save(any(CustomerOrder.class));
            verify(cartService, times(1)).clearCurrentCart();
        }
    }

    @Test
    void testCheckout_EmptyCart() {
        // Arrange
        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        emptyCart.setUser(testUser);
        emptyCart.setItems(new ArrayList<>());

        when(cartService.getCurrentUserCart()).thenReturn(emptyCart);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            orderService.checkout();
        });
        assertEquals("Cannot checkout with an empty cart", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testMyOrders_Success() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setUser(testUser);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(2000.0);
        order.setItems(new ArrayList<>());

        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of(order));

            // Act
            List<OrderResponse> result = orderService.myOrders();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getOrderId());
            verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
        }
    }

    @Test
    void testMyOrders_EmptyList() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(new ArrayList<>());

            // Act
            List<OrderResponse> result = orderService.myOrders();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.size());
        }
    }

    @Test
    void testCheckout_CalculatesTotalCorrectly() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(100.0);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(200.0);

        CartItem item1 = new CartItem();
        item1.setProduct(product1);
        item1.setQuantity(2); // 2 * 100 = 200

        CartItem item2 = new CartItem();
        item2.setProduct(product2);
        item2.setQuantity(1); // 1 * 200 = 200

        Cart cartWithMultipleItems = new Cart();
        cartWithMultipleItems.setId(1L);
        cartWithMultipleItems.setUser(testUser);
        cartWithMultipleItems.setItems(List.of(item1, item2));

        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("test@example.com");
            when(cartService.getCurrentUserCart()).thenReturn(cartWithMultipleItems);
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            CustomerOrder savedOrder = new CustomerOrder();
            savedOrder.setId(1L);
            savedOrder.setUser(testUser);
            savedOrder.setStatus(OrderStatus.CREATED);
            savedOrder.setTotalAmount(400.0); // (2*100) + (1*200) = 400
            savedOrder.setItems(new ArrayList<>());

            when(orderRepository.save(any(CustomerOrder.class))).thenReturn(savedOrder);

            // Act
            OrderResponse result = orderService.checkout();

            // Assert
            assertEquals(400.0, result.getTotalAmount());
        }
    }

    @Test
    void testMyOrders_UserNotFound() {
        // Arrange
        try (MockedStatic<SecurityUtils> mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("nonexistent@example.com");
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                orderService.myOrders();
            });
        }
    }
}

