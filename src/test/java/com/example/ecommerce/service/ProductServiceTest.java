package com.example.ecommerce.service;

import com.example.ecommerce.dto.product.ProductRequest;
import com.example.ecommerce.dto.product.ProductResponse;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test data
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Laptop");
        testProduct.setPrice(999.99);

        productRequest = new ProductRequest();
        productRequest.setName("New Laptop");
        productRequest.setPrice(1499.99);
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(999.99);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(499.99);

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        // Act
        List<ProductResponse> result = productService.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Phone", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProductResponse> result = productService.getAll();

        // Assert
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testSaveProduct_Success() {
        // Arrange
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("New Laptop");
        savedProduct.setPrice(1499.99);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ProductResponse result = productService.save(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New Laptop", result.getName());
        assertEquals(1499.99, result.getPrice());
        assertEquals(1L, result.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testFindEntityById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.findEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Laptop", result.getName());
        assertEquals(999.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindEntityById_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findEntityById(999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testSaveProduct_WithCorrectPrice() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("iPhone 15");
        request.setPrice(50000);

        Product savedProduct = new Product();
        savedProduct.setId(2L);
        savedProduct.setName("iPhone 15");
        savedProduct.setPrice(50000);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ProductResponse result = productService.save(request);

        // Assert
        assertEquals(50000, result.getPrice());
        assertEquals("iPhone 15", result.getName());
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        ProductResponse result = productService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Laptop", result.getName());
        assertEquals(999.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getById(999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }
}

