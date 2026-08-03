package com.ecommerce.product_service.config;

import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        loadDefaults(false);
    }

    public void reset() throws Exception {
        loadDefaults(true);
    }

    private void loadDefaults(boolean replaceExisting) throws Exception {
        try (var input = new ClassPathResource("data/products.json").getInputStream()) {
            Product[] products = objectMapper.readValue(input, Product[].class);
            if (replaceExisting) {
                productRepository.deleteAll();
                productRepository.saveAll(Arrays.asList(products));
                return;
            }

            productRepository.saveAll(Arrays.stream(products)
                    .filter(product -> !productRepository.existsById(product.getId()))
                    .toList());
        }
    }
}
