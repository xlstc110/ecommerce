package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.model.Product;

public class ProductMapper {
    public static Product productRequestToProduct(ProductRequest productRequest) {
        return Product.builder()
                .id(productRequest.id())
                .image(productRequest.image())
                .name(productRequest.name())
                .rating(productRequest.rating())
                .priceCents(productRequest.priceCents())
                .keywords(productRequest.keywords())
                .build();
    }

    public static ProductResponse productToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getImage(),
                product.getName(),
                product.getRating(),
                product.getPriceCents(),
                product.getKeywords()
        );
    }
}
