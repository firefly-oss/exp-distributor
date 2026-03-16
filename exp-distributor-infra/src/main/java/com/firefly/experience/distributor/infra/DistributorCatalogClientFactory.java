package com.firefly.experience.distributor.infra;

import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.api.ShipmentQueriesApi;
import com.firefly.domain.distributor.catalog.sdk.api.SimulationsApi;
import com.firefly.domain.distributor.catalog.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DistributorCatalogClientFactory {

    private final ApiClient apiClient;

    public DistributorCatalogClientFactory(DistributorCatalogProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public com.firefly.domain.distributor.catalog.sdk.api.DistributorApi catalogDistributorApi() {
        return new DistributorApi(apiClient);
    }

    @Bean
    public SimulationsApi simulationsApi() {
        return new SimulationsApi(apiClient);
    }

    /**
     * Provides the {@link ShipmentQueriesApi} bean for shipment query and mutation operations
     * (getShipment, updateShipment, deleteShipment, updateShipmentStatus).
     *
     * @return a ready-to-use ShipmentQueriesApi instance
     */
    @Bean
    public ShipmentQueriesApi shipmentQueriesApi() {
        return new ShipmentQueriesApi(apiClient);
    }
}
