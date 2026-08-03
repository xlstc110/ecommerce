package com.ecommerce.cart_service.config;

import com.ecommerce.cart_service.client.ProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {
    @Bean
    ProductClient productClient(
            RestClient.Builder builder,
            @Value("${product.url}") String productUrl) {
        RestClient client = builder.baseUrl(productUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(ProductClient.class);
    }
}
