package com.product.service;

import com.product.dto.ProductRequest;
import com.product.dto.ProductResponse;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> allProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()
                ))
                .collect(Collectors.toList());
    }

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);

    }

    public ProductResponse findByProductId(String productId) {
        return productRepository.findById(productId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    private ProductResponse convertToResponse(Product product) {

        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }



}
