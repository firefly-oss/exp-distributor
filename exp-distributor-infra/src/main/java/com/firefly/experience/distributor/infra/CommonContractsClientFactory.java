package com.firefly.experience.distributor.infra;

import com.firefly.domain.common.contracts.sdk.api.ContractsApi;
import com.firefly.domain.common.contracts.sdk.api.ContractTermsApi;
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
     * Provides the {@link ContractsApi} bean for contract operations.
     *
     * @return a ready-to-use ContractsApi instance
     */
    @Bean
    public ContractsApi contractsApi() {
        return new ContractsApi(apiClient);
    }

    /**
     * Provides the {@link ContractTermsApi} bean for contract terms operations.
     *
     * @return a ready-to-use ContractTermsApi instance
     */
    @Bean
    public ContractTermsApi contractTermsApi() {
        return new ContractTermsApi(apiClient);
    }
}
