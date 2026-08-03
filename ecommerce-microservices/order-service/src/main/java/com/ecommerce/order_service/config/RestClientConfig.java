package com.ecommerce.order_service.config;

import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.client.CartClient;
import com.ecommerce.order_service.client.ProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {
    @Value("${inventory.url}")
    private String inventoryServiceUrl;

    @Bean
    public InventoryClient inventoryClient(
            RestClient.Builder restClientBuilder) {

        RestClient restClient = restClientBuilder
                .baseUrl(inventoryServiceUrl)
                .build();

        RestClientAdapter adapter =
                RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(InventoryClient.class);
    }

    @Bean
    public CartClient cartClient(
            RestClient.Builder builder,
            @Value("${cart.url}") String cartUrl) {
        return createClient(builder, cartUrl, CartClient.class);
    }

    @Bean
    public ProductClient productClient(
            RestClient.Builder builder,
            @Value("${product.url}") String productUrl) {
        return createClient(builder, productUrl, ProductClient.class);
    }

    private <T> T createClient(
            RestClient.Builder builder,
            String baseUrl,
            Class<T> clientType) {
        RestClient client = builder.baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(client);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build()
                .createClient(clientType);
    }
}
