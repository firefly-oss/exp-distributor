package com.firefly.experience.distributor.infra;

import com.firefly.domain.distributor.branding.sdk.api.AgencyApi;
import com.firefly.domain.distributor.branding.sdk.api.AgentAgencyApi;
import com.firefly.domain.distributor.branding.sdk.api.AgentApi;
import com.firefly.domain.distributor.branding.sdk.api.ConfigurationApi;
import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.api.EligibilityApi;
import com.firefly.domain.distributor.branding.sdk.api.FeesApi;
import com.firefly.domain.distributor.branding.sdk.api.OperationApi;
import com.firefly.domain.distributor.branding.sdk.api.PricingApi;
import com.firefly.domain.distributor.branding.sdk.api.TermsAndConditionsApi;
import com.firefly.domain.distributor.branding.sdk.api.TerritoryApi;
import com.firefly.domain.distributor.branding.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Factory that creates and configures the Distributor Branding SDK {@link ApiClient}
 * and exposes domain API beans for dependency injection.
 */
@Component
public class DistributorBrandingClientFactory {

    private final ApiClient apiClient;

    /**
     * Initialises the API client with the base path from configuration properties.
     *
     * @param properties connection properties for the Distributor Branding service
     */
    public DistributorBrandingClientFactory(DistributorBrandingProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    /**
     * Provides the {@link DistributorApi} bean for distributor profile operations.
     *
     * @return a ready-to-use DistributorApi instance
     */
    @Bean
    public DistributorApi brandingDistributorApi() {
        return new DistributorApi(apiClient);
    }

    /**
     * Provides the {@link AgencyApi} bean for agency management operations.
     *
     * @return a ready-to-use AgencyApi instance
     */
    @Bean
    public AgencyApi agencyApi() {
        return new AgencyApi(apiClient);
    }

    /**
     * Provides the {@link AgentApi} bean for agent management operations.
     *
     * @return a ready-to-use AgentApi instance
     */
    @Bean
    public AgentApi agentApi() {
        return new AgentApi(apiClient);
    }

    /**
     * Provides the {@link AgentAgencyApi} bean for agent-agency assignment operations.
     *
     * @return a ready-to-use AgentAgencyApi instance
     */
    @Bean
    public AgentAgencyApi agentAgencyApi() {
        return new AgentAgencyApi(apiClient);
    }

    /**
     * Provides the {@link TerritoryApi} bean for territory management operations.
     *
     * @return a ready-to-use TerritoryApi instance
     */
    @Bean
    public TerritoryApi territoryApi() {
        return new TerritoryApi(apiClient);
    }

    /**
     * Provides the {@link TermsAndConditionsApi} bean for terms and conditions operations.
     *
     * @return a ready-to-use TermsAndConditionsApi instance
     */
    @Bean
    public TermsAndConditionsApi termsAndConditionsApi() {
        return new TermsAndConditionsApi(apiClient);
    }

    /**
     * Provides the {@link OperationApi} bean for distributor operation management.
     *
     * @return a ready-to-use OperationApi instance
     */
    @Bean
    public OperationApi operationApi() {
        return new OperationApi(apiClient);
    }

    /**
     * Provides the {@link ConfigurationApi} bean for distributor configuration operations.
     *
     * @return a ready-to-use ConfigurationApi instance
     */
    @Bean
    public ConfigurationApi configurationApi() {
        return new ConfigurationApi(apiClient);
    }

    /**
     * Provides the {@link EligibilityApi} bean for distributor eligibility operations.
     *
     * @return a ready-to-use EligibilityApi instance
     */
    @Bean
    public EligibilityApi brandingEligibilityApi() {
        return new EligibilityApi(apiClient);
    }

    /**
     * Provides the {@link FeesApi} bean for distributor fee operations.
     *
     * @return a ready-to-use FeesApi instance
     */
    @Bean
    public FeesApi brandingFeesApi() {
        return new FeesApi(apiClient);
    }

    /**
     * Provides the {@link PricingApi} bean for distributor pricing operations.
     *
     * @return a ready-to-use PricingApi instance
     */
    @Bean
    public PricingApi brandingPricingApi() {
        return new PricingApi(apiClient);
    }
}
