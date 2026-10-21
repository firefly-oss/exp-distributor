/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.experience.distributor.infra;

import io.fireflysoftware.domain.distributor.catalog.sdk.api.DistributorApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.api.ShipmentQueriesApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.api.SimulationsApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.invoker.ApiClient;
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
    public io.fireflysoftware.domain.distributor.catalog.sdk.api.DistributorApi catalogDistributorApi() {
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
