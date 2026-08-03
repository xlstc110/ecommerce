package com.ecommerce.cart_service.repository;

import com.ecommerce.cart_service.model.DeliveryOption;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DeliveryOptionRepository extends MongoRepository<DeliveryOption, String> {
    List<DeliveryOption> findAllByOrderByIdAsc();
}
