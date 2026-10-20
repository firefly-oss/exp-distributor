package io.fireflysoftware.experience.distributor.infra;

import io.fireflysoftware.domain.product.pricing.sdk.api.EligibilityApi;
import io.fireflysoftware.domain.product.pricing.sdk.api.FeesApi;
import io.fireflysoftware.domain.product.pricing.sdk.api.PricingApi;
import io.fireflysoftware.domain.product.pricing.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ProductPricingClientFactory {

    private final ApiClient apiClient;

    public ProductPricingClientFactory(ProductPricingProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public PricingApi pricingApi() {
        return new PricingApi(apiClient);
    }

    @Bean
    public EligibilityApi eligibilityApi() {
        return new EligibilityApi(apiClient);
    }

    @Bean
    public FeesApi feesApi() {
        return new FeesApi(apiClient);
    }
}
