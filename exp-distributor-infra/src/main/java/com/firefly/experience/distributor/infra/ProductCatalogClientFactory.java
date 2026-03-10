package com.firefly.experience.distributor.infra;

import com.firefly.domain.product.catalog.sdk.api.ProductsApi;
import com.firefly.domain.product.catalog.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ProductCatalogClientFactory {

    private final ApiClient apiClient;

    public ProductCatalogClientFactory(ProductCatalogProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public ProductsApi productsApi() {
        return new ProductsApi(apiClient);
    }
}
