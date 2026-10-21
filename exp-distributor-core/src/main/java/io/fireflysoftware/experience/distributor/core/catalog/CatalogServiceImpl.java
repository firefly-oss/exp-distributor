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


package io.fireflysoftware.experience.distributor.core.catalog;

import io.fireflysoftware.domain.distributor.catalog.sdk.api.DistributorApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.model.RegisterProductCommand;
import io.fireflysoftware.domain.distributor.catalog.sdk.model.UpdateProductCommand;
import io.fireflysoftware.domain.distributor.catalog.sdk.model.UpdateProductInfoCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AddCatalogItemRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CatalogItemDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateCatalogItemRequest;
import io.fireflysoftware.experience.distributor.core.mappers.CatalogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class CatalogServiceImpl implements CatalogService {

    private final DistributorApi catalogDistributorApi;
    private final CatalogMapper catalogMapper;

    public CatalogServiceImpl(@Qualifier("catalogDistributorApi") DistributorApi catalogDistributorApi,
                              CatalogMapper catalogMapper) {
        this.catalogDistributorApi = catalogDistributorApi;
        this.catalogMapper = catalogMapper;
    }

    @Override
    public Flux<CatalogItemDTO> listCatalog(UUID distributorId) {
        log.info("Listing catalog items for distributor: {}", distributorId);
        return catalogDistributorApi.listCatalog(distributorId, null)
                .map(catalogMapper::toDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> addToCatalog(UUID distributorId, AddCatalogItemRequest request) {
        log.info("Adding catalog item for distributor: {}", distributorId);
        RegisterProductCommand cmd = new RegisterProductCommand();
        return catalogDistributorApi.registerProduct(distributorId, cmd, UUID.randomUUID().toString())
                .map(result -> (UUID) result);
    }

    @Override
    public Mono<CatalogItemDTO> getCatalogItem(UUID distributorId, UUID catalogItemId) {
        log.info("Getting catalog item {} for distributor: {}", catalogItemId, distributorId);
        return catalogDistributorApi.listCatalog(distributorId, null)
                .filter(p -> catalogItemId.equals(p.getId()))
                .next()
                .map(catalogMapper::toDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> updateCatalogItem(UUID distributorId, UUID catalogItemId, UpdateCatalogItemRequest request) {
        log.info("Updating catalog item {} for distributor: {}", catalogItemId, distributorId);
        UpdateProductCommand cmd = new UpdateProductCommand();
        return catalogDistributorApi.reviseProduct(distributorId, catalogItemId, cmd, UUID.randomUUID().toString())
                .map(result -> (UUID) result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> removeFromCatalog(UUID distributorId, UUID catalogItemId) {
        log.info("Removing catalog item {} for distributor: {}", catalogItemId, distributorId);
        UpdateProductInfoCommand cmd = new UpdateProductInfoCommand();
        return catalogDistributorApi.retireProduct(distributorId, catalogItemId, cmd, UUID.randomUUID().toString())
                .map(result -> (UUID) result);
    }

}
