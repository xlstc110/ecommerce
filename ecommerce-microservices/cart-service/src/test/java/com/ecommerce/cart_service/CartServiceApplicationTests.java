package com.ecommerce.cart_service;

import com.ecommerce.cart_service.client.ProductClient;
import com.ecommerce.cart_service.dto.ProductResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.mongodb.MongoDBContainer;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartServiceApplicationTests {
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    static {
        mongoDBContainer.start();
    }

    @LocalServerPort
    private Integer port;

    @MockitoBean
    private ProductClient productClient;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        when(jwtDecoder.decode(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            String subject = "demo-token".equals(token) ? "demo-user" : token;
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", subject)
                    .build();
        });
        when(productClient.getProduct(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            int price = id.startsWith("e436") ? 1090 : 2095;
            return new ProductResponse(
                    id,
                    "images/products/test.jpg",
                    "Test product",
                    new ProductResponse.Rating(4.5, 10),
                    price,
                    List.of("test")
            );
        });
    }

    @Test
    void shouldReturnExpandedSeededCartAndPaymentSummary() {
        RestAssured.given()
                .auth().oauth2("demo-token")
                .queryParam("expand", "product")
                .when()
                .get("/api/cart-items")
                .then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("[0].product.id", equalTo("e43638ce-6aa0-4b85-b27f-e1d07eb678c6"));

        RestAssured.given()
                .auth().oauth2("demo-token")
                .when()
                .get("/api/payment-summary")
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(3))
                .body("totalCostCents", equalTo(5251));
    }

    @Test
    void shouldRejectInvalidQuantity() {
        RestAssured.given()
                .auth().oauth2("demo-token")
                .contentType("application/json")
                .body("""
                        {
                          "productId": "e43638ce-6aa0-4b85-b27f-e1d07eb678c6",
                          "quantity": 11
                        }
                        """)
                .when()
                .post("/api/cart-items")
                .then()
                .statusCode(400)
                .body("error", equalTo("Quantity must be a number between 1 and 10"));
    }

    @Test
    void shouldKeepCartsIsolatedByJwtSubject() {
        RestAssured.given()
                .auth().oauth2("second-user")
                .contentType("application/json")
                .body("""
                        {
                          "productId": "e43638ce-6aa0-4b85-b27f-e1d07eb678c6",
                          "quantity": 1
                        }
                        """)
                .when()
                .post("/api/cart-items")
                .then()
                .statusCode(201);

        RestAssured.given()
                .auth().oauth2("second-user")
                .when()
                .get("/api/cart-items")
                .then()
                .statusCode(200)
                .body("", hasSize(1));

        RestAssured.given()
                .auth().oauth2("demo-token")
                .when()
                .get("/api/cart-items")
                .then()
                .statusCode(200)
                .body("", hasSize(2));
    }

    @Test
    void shouldRequireAuthenticationForCart() {
        RestAssured.when()
                .get("/api/cart-items")
                .then()
                .statusCode(401);
    }
}
