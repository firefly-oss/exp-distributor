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
        return catalogDistributorApi.listCatalog(distributorId)
                .map(catalogMapper::toDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> addToCatalog(UUID distributorId, AddCatalogItemRequest request) {
        log.info("Adding catalog item for distributor: {}", distributorId);
        RegisterProductCommand cmd = new RegisterProductCommand();
        // RegisterProductCommand is a composite command; set available sub-fields as needed
        return catalogDistributorApi.registerProduct(distributorId, cmd)
                .map(result -> (UUID) result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> updateCatalogItem(UUID distributorId, UUID productId, UpdateCatalogItemRequest request) {
        log.info("Updating catalog item {} for distributor: {}", productId, distributorId);
        UpdateProductCommand cmd = new UpdateProductCommand();
        // UpdateProductCommand is a composite command; set available sub-fields as needed
        return catalogDistributorApi.reviseProduct(distributorId, productId, cmd)
                .map(result -> (UUID) result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> removeFromCatalog(UUID distributorId, UUID productId) {
        log.info("Removing catalog item {} for distributor: {}", productId, distributorId);
        UpdateProductInfoCommand cmd = new UpdateProductInfoCommand();
        return catalogDistributorApi.retireProduct(distributorId, productId, cmd)
                .map(result -> (UUID) result);
    }

}
