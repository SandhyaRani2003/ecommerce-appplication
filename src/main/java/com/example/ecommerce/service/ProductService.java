package com.example.ecommerce.service;

import com.example.ecommerce.dto.product.ProductRequest;
import com.example.ecommerce.dto.product.ProductResponse;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;

    public List<ProductResponse> getAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getById(Long productId) {
        return toResponse(findEntityById(productId));
    }

    public ProductResponse save(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        return toResponse(repo.save(product));
    }

    public Product findEntityById(Long productId) {
        return repo.findById(productId)
                .orElseThrow(() -> new com.example.ecommerce.exception.ResourceNotFoundException("Product not found"));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }
}
