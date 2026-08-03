package com.ecommerce.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.*;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class Routes {
    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return route("product_service")
                .route(
                        path("/api/product")
                                .or(path("/api/products"))
                                .or(path("/api/products/**")),
                        http()
                )
                .before(uri("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "productServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")
                ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return route("product_service_swagger")
                .route(path("/aggregate/product-service/v3/api-docs"), http())
                .before(uri("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "productServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")
                ))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return route("order_service")
                .route(
                        path("/api/order")
                                .or(path("/api/orders"))
                                .or(path("/api/orders/**")),
                        http()
                )
                .before(uri("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")
                ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return route("order_service_swagger")
                .route(
                        path("/aggregate/order-service/v3/api-docs"),
                        http()
                )
                .before(uri("http://localhost:8081"))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return route("inventory_service")
                .route(path("/api/inventory"), http())
                .before(uri("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "inventoryServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")
                ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartServiceRoute() {
        return route("cart_service")
                .route(
                        path("/api/cart-items")
                                .or(path("/api/cart-items/**"))
                                .or(path("/api/delivery-options"))
                                .or(path("/api/payment-summary")),
                        http()
                )
                .before(uri("http://localhost:8083"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "cartServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")
                ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartServiceSwaggerRoute() {
        return route("cart_service_swagger")
                .route(path("/aggregate/cart-service/v3/api-docs"), http())
                .before(uri("http://localhost:8083"))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return route("inventory_service_swagger")
                .route(
                        path("/aggregate/inventory-service/v3/api-docs"),
                        http()
                )
                .before(uri("http://localhost:8082"))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        //Replacement for:
        //@RestController
        //public class FallbackController {
        //
        //    @GetMapping("/fallbackRoute")
        //    public ResponseEntity<String> fallback() {
        //        return ResponseEntity
        //                .status(HttpStatus.SERVICE_UNAVAILABLE)
        //                .body("Service Unavailable");
        //    }
        //}
        return route("fallbackRoute")
                .GET("/fallbackRoute", request ->
                        ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Service Unavailable"))
                .build();
    }
}
