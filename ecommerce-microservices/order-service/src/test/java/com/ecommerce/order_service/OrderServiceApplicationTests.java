package com.ecommerce.order_service;

import com.ecommerce.order_service.client.CartClient;
import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.CheckoutSnapshot;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.mysql.MySQLContainer;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {
    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

    static {
        mySQLContainer.start();
    }

    @LocalServerPort
    private Integer port;

    @MockitoBean
    private CartClient cartClient;
    @MockitoBean
    private InventoryClient inventoryClient;
    @MockitoBean
    private ProductClient productClient;
    @MockitoBean
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        when(cartClient.getCheckoutSnapshot()).thenReturn(new CheckoutSnapshot(
                3506,
                List.of(new CheckoutSnapshot.Item(
                        "e43638ce-6aa0-4b85-b27f-e1d07eb678c6",
                        1,
                        1723716000000L
                ))
        ));
        when(inventoryClient.isInStock(any(), any())).thenReturn(true);
    }

    @Test
    void shouldCreateOrderFromCartAndClearCart() {
        RestAssured.when()
                .post("/api/orders")
                .then()
                .statusCode(201)
                .body("totalCostCents", equalTo(3506))
                .body("products[0].productId",
                        equalTo("e43638ce-6aa0-4b85-b27f-e1d07eb678c6"));

        verify(cartClient).clearCart();
        verify(kafkaTemplate).send(eq("order-placed"), any(OrderPlacedEvent.class));
    }
}
