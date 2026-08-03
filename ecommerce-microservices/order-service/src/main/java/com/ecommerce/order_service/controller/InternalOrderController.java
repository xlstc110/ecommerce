package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.service.DefaultOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalOrderController {
    private final DefaultOrderService defaultOrderService;

    @PostMapping("/internal/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() {
        defaultOrderService.reset();
    }
}
