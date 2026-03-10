package com.firefly.experience.distributor.infra;

import com.firefly.domain.common.contracts.sdk.api.ContractDocumentsApi;
import com.firefly.domain.common.contracts.sdk.api.ContractPartiesApi;
import com.firefly.domain.common.contracts.sdk.api.ContractSignaturesApi;
import com.firefly.domain.common.contracts.sdk.api.ContractTermsApi;
import com.firefly.domain.common.contracts.sdk.api.ContractsApi;
import com.firefly.domain.common.contracts.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CommonContractsClientFactory {

    private final ApiClient apiClient;

    public CommonContractsClientFactory(CommonContractsProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public ContractsApi contractsApi() {
        return new ContractsApi(apiClient);
    }

    @Bean
    public ContractPartiesApi contractPartiesApi() {
        return new ContractPartiesApi(apiClient);
    }

    @Bean
    public ContractTermsApi contractTermsApi() {
        return new ContractTermsApi(apiClient);
    }

    @Bean
    public ContractDocumentsApi contractDocumentsApi() {
        return new ContractDocumentsApi(apiClient);
    }

    @Bean
    public ContractSignaturesApi contractSignaturesApi() {
        return new ContractSignaturesApi(apiClient);
    }
}
