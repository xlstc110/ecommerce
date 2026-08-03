package com.ecommerce.order_service;

import com.ecommerce.order_service.client.CartClient;
import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.CheckoutSnapshot;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.repository.OrderRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.mysql.MySQLContainer;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {
    private static final String FIRST_AUTHORIZATION = "Bearer first-token";
    private static final String SECOND_AUTHORIZATION = "Bearer second-token";

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
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        orderRepository.deleteAll();
        when(jwtDecoder.decode(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", token.replace("-token", "-user"))
                    .build();
        });
        when(cartClient.getCheckoutSnapshot(anyString())).thenReturn(new CheckoutSnapshot(
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
        RestAssured.given()
                .header("Authorization", FIRST_AUTHORIZATION)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(201)
                .body("totalCostCents", equalTo(3506))
                .body("products[0].productId",
                        equalTo("e43638ce-6aa0-4b85-b27f-e1d07eb678c6"));

        verify(cartClient).clearCart(FIRST_AUTHORIZATION);
        verify(kafkaTemplate).send(
                eq("order-placed"),
                argThat(event -> "first-user".equals(event.getUserId())));
    }

    @Test
    void shouldKeepOrdersIsolatedByJwtSubject() {
        String firstOrderId = createOrder(FIRST_AUTHORIZATION);
        String secondOrderId = createOrder(SECOND_AUTHORIZATION);

        RestAssured.given()
                .header("Authorization", FIRST_AUTHORIZATION)
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].id", equalTo(firstOrderId));

        RestAssured.given()
                .header("Authorization", SECOND_AUTHORIZATION)
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].id", equalTo(secondOrderId));

        RestAssured.given()
                .header("Authorization", FIRST_AUTHORIZATION)
                .when()
                .get("/api/orders/{orderId}", secondOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldRequireAuthenticationForOrders() {
        RestAssured.when()
                .get("/api/orders")
                .then()
                .statusCode(401);
    }

    private String createOrder(String authorization) {
        return RestAssured.given()
                .header("Authorization", authorization)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
