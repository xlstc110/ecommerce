package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = ProductMapper.productRequestToProduct(productRequest);
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully");
        return ProductMapper.productToProductResponse(savedProduct);
    }

    public List<ProductResponse> getProducts() {
        List<Product> products = productRepository.findAll();
        return products
                .stream()
                .map(ProductMapper::productToProductResponse)
                .toList();
    }
}
