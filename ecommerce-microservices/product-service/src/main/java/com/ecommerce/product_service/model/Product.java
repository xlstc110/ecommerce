package com.ecommerce.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {
    @Id
    private String id;
    private String image;
    private String name;
    private Rating rating;
    private Integer priceCents;
    private List<String> keywords;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rating {
        private Double stars;
        private Integer count;
    }
}
