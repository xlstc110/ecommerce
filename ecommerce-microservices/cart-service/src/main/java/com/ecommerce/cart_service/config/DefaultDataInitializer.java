package com.ecommerce.cart_service.config;

import com.ecommerce.cart_service.model.CartItem;
import com.ecommerce.cart_service.model.DeliveryOption;
import com.ecommerce.cart_service.repository.CartItemRepository;
import com.ecommerce.cart_service.repository.DeliveryOptionRepository;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DefaultDataInitializer implements CommandLineRunner {
    private final CartItemRepository cartItemRepository;
    private final DeliveryOptionRepository deliveryOptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        loadDefaults(false);
    }

    public void reset() throws Exception {
        loadDefaults(true);
    }

    private void loadDefaults(boolean replaceExisting) throws Exception {
        if (replaceExisting) {
            cartItemRepository.deleteAll();
            deliveryOptionRepository.deleteAll();
        }

        try (var input = new ClassPathResource("data/delivery-options.json").getInputStream()) {
            DeliveryOption[] options = objectMapper.readValue(input, DeliveryOption[].class);
            deliveryOptionRepository.saveAll(Arrays.stream(options)
                    .filter(option -> !deliveryOptionRepository.existsById(option.getId()))
                    .toList());
        }
        if (replaceExisting || cartItemRepository.count() == 0) {
            try (var input = new ClassPathResource("data/cart.json").getInputStream()) {
                CartItem[] items = objectMapper.readValue(input, CartItem[].class);
                long timestamp = System.currentTimeMillis();
                for (int index = 0; index < items.length; index++) {
                    items[index].setCreatedAt(timestamp + index);
                }
                cartItemRepository.saveAll(Arrays.asList(items));
            }
        }
    }
}
