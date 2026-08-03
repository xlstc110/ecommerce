package com.ecommerce.cart_service.repository;

import com.ecommerce.cart_service.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartItemRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findAllByOrderByCreatedAtAsc();
}
