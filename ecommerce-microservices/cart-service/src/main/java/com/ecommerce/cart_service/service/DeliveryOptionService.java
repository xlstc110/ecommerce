package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.DeliveryOptionResponse;
import com.ecommerce.cart_service.model.DeliveryOption;
import com.ecommerce.cart_service.repository.DeliveryOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class DeliveryOptionService {
    private final DeliveryOptionRepository repository;
    private final Clock clock = Clock.systemUTC();

    public List<DeliveryOptionResponse> getDeliveryOptions(boolean includeEstimate) {
        return repository.findAllByOrderByIdAsc().stream()
                .map(option -> toResponse(option, includeEstimate))
                .toList();
    }

    public DeliveryOption require(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid delivery option"));
    }

    public long estimatedDeliveryTime(DeliveryOption option) {
        return clock.millis() + Duration.ofDays(option.getDeliveryDays()).toMillis();
    }

    private DeliveryOptionResponse toResponse(DeliveryOption option, boolean includeEstimate) {
        return new DeliveryOptionResponse(
                option.getId(),
                option.getDeliveryDays(),
                option.getPriceCents(),
                includeEstimate ? estimatedDeliveryTime(option) : null
        );
    }
}
