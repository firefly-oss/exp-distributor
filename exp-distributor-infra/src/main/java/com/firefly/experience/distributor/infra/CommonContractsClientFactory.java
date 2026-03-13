package com.firefly.experience.distributor.infra;

import com.firefly.domain.common.contracts.sdk.api.ScaOperationsApi;
import com.firefly.domain.common.contracts.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Factory that creates and configures the Common Contracts SDK {@link ApiClient}
 * and exposes domain API beans for dependency injection.
 */
@Component
public class CommonContractsClientFactory {

    private final ApiClient apiClient;

    /**
     * Initialises the API client with the base path from configuration properties.
     *
     * @param properties connection properties for the Common Contracts service
     */
    public CommonContractsClientFactory(CommonContractsProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    /**
     * Provides the {@link ScaOperationsApi} bean for Strong Customer Authentication operations.
     *
     * @return a ready-to-use ScaOperationsApi instance
     */
    @Bean
    public ScaOperationsApi scaOperationsApi() {
        return new ScaOperationsApi(apiClient);
    }
}
