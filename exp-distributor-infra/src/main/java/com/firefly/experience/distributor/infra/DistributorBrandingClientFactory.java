package com.firefly.experience.distributor.infra;

import com.firefly.domain.distributor.branding.sdk.api.AgencyApi;
import com.firefly.domain.distributor.branding.sdk.api.AgentAgencyApi;
import com.firefly.domain.distributor.branding.sdk.api.AgentApi;
import com.firefly.domain.distributor.branding.sdk.api.ConfigurationApi;
import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.api.OperationApi;
import com.firefly.domain.distributor.branding.sdk.api.TermsAndConditionsApi;
import com.firefly.domain.distributor.branding.sdk.api.TerritoryApi;
import com.firefly.domain.distributor.branding.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DistributorBrandingClientFactory {

    private final ApiClient apiClient;

    public DistributorBrandingClientFactory(DistributorBrandingProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public DistributorApi brandingDistributorApi() {
        return new DistributorApi(apiClient);
    }

    @Bean
    public AgencyApi agencyApi() {
        return new AgencyApi(apiClient);
    }

    @Bean
    public AgentApi agentApi() {
        return new AgentApi(apiClient);
    }

    @Bean
    public AgentAgencyApi agentAgencyApi() {
        return new AgentAgencyApi(apiClient);
    }

    @Bean
    public TerritoryApi territoryApi() {
        return new TerritoryApi(apiClient);
    }

    @Bean
    public TermsAndConditionsApi termsAndConditionsApi() {
        return new TermsAndConditionsApi(apiClient);
    }

    @Bean
    public OperationApi operationApi() {
        return new OperationApi(apiClient);
    }

    @Bean
    public ConfigurationApi configurationApi() {
        return new ConfigurationApi(apiClient);
    }
}
