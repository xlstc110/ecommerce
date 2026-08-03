package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.config.ProductDataInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalProductController {
    private final ProductDataInitializer productDataInitializer;

    @PostMapping("/internal/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() throws Exception {
        productDataInitializer.reset();
    }
}
