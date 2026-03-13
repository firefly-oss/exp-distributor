package com.firefly.experience.distributor.core.catalog;

import com.firefly.experience.distributor.interfaces.dtos.AddCatalogItemRequest;
import com.firefly.experience.distributor.interfaces.dtos.CatalogItemDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateCatalogItemRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CatalogService {

    Flux<CatalogItemDTO> listCatalog(UUID distributorId);

    Mono<UUID> addToCatalog(UUID distributorId, AddCatalogItemRequest request);

    Mono<CatalogItemDTO> getCatalogItem(UUID distributorId, UUID catalogItemId);

    Mono<UUID> updateCatalogItem(UUID distributorId, UUID catalogItemId, UpdateCatalogItemRequest request);

    Mono<UUID> removeFromCatalog(UUID distributorId, UUID catalogItemId);
}
