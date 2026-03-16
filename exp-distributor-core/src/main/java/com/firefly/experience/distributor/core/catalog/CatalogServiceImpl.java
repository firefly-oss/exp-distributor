package com.firefly.experience.distributor.core.catalog;

import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.model.RegisterProductCommand;
import com.firefly.domain.distributor.catalog.sdk.model.UpdateProductCommand;
import com.firefly.domain.distributor.catalog.sdk.model.UpdateProductInfoCommand;
import com.firefly.experience.distributor.interfaces.dtos.AddCatalogItemRequest;
import com.firefly.experience.distributor.interfaces.dtos.CatalogItemDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateCatalogItemRequest;
import com.firefly.experience.distributor.core.mappers.CatalogMapper;
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
        return catalogDistributorApi.listCatalog(distributorId, UUID.randomUUID().toString())
                .map(catalogMapper::toDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> addToCatalog(UUID distributorId, AddCatalogItemRequest request) {
        log.info("Adding catalog item for distributor: {}", distributorId);
        RegisterProductCommand cmd = new RegisterProductCommand();
        // RegisterProductCommand is a composite command; set available sub-fields as needed
        // ARCH-EXCEPTION: domain-distributor-catalog-sdk generated client does not expose an
        // xIdempotencyKey parameter on registerProduct; idempotency cannot be set at call-site.
        return catalogDistributorApi.registerProduct(distributorId, cmd, UUID.randomUUID().toString())
                .map(result -> (UUID) result);
    }

    @Override
    public Mono<CatalogItemDTO> getCatalogItem(UUID distributorId, UUID catalogItemId) {
        log.info("Getting catalog item {} for distributor: {}", catalogItemId, distributorId);
        return catalogDistributorApi.listCatalog(distributorId, UUID.randomUUID().toString())
                .filter(p -> catalogItemId.equals(p.getId()))
                .next()
                .map(catalogMapper::toDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> updateCatalogItem(UUID distributorId, UUID catalogItemId, UpdateCatalogItemRequest request) {
        log.info("Updating catalog item {} for distributor: {}", catalogItemId, distributorId);
        UpdateProductCommand cmd = new UpdateProductCommand();
        // UpdateProductCommand is a composite command; set available sub-fields as needed
        // ARCH-EXCEPTION: domain-distributor-catalog-sdk generated client does not expose an
        // xIdempotencyKey parameter on reviseProduct; idempotency cannot be set at call-site.
        return catalogDistributorApi.reviseProduct(distributorId, catalogItemId, cmd, UUID.randomUUID().toString())
                .map(result -> (UUID) result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> removeFromCatalog(UUID distributorId, UUID catalogItemId) {
        log.info("Removing catalog item {} for distributor: {}", catalogItemId, distributorId);
        UpdateProductInfoCommand cmd = new UpdateProductInfoCommand();
        // ARCH-EXCEPTION: domain-distributor-catalog-sdk generated client does not expose an
        // xIdempotencyKey parameter on retireProduct; idempotency cannot be set at call-site.
        return catalogDistributorApi.retireProduct(distributorId, catalogItemId, cmd, UUID.randomUUID().toString())
                .map(result -> (UUID) result);
    }

}
