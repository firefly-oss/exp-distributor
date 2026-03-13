package com.firefly.experience.distributor.core.products;

import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.model.RegisterProductCommand;
import com.firefly.domain.distributor.catalog.sdk.model.UpdateProductCommand;
import com.firefly.domain.distributor.catalog.sdk.model.UpdateProductInfoCommand;
import com.firefly.domain.product.catalog.sdk.api.ProductsApi;
import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateProductRequest;
import com.firefly.experience.distributor.core.mappers.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final DistributorApi catalogDistributorApi;
    private final ProductsApi productsApi;
    private final ProductMapper productMapper;

    public ProductServiceImpl(@Qualifier("catalogDistributorApi") DistributorApi catalogDistributorApi,
                              ProductsApi productsApi,
                              ProductMapper productMapper) {
        this.catalogDistributorApi = catalogDistributorApi;
        this.productsApi = productsApi;
        this.productMapper = productMapper;
    }

    @Override
    public Flux<ProductDetailDTO> listProducts(UUID distributorId) {
        log.info("Listing products for distributor: {}", distributorId);
        return catalogDistributorApi.listCatalog(distributorId)
                .map(productMapper::fromCatalogDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> registerProduct(UUID distributorId, RegisterProductRequest request) {
        log.info("Registering product for distributor: {}", distributorId);
        RegisterProductCommand cmd = new RegisterProductCommand();
        // ARCH-EXCEPTION: domain-distributor-catalog-sdk generated client does not expose an
        // xIdempotencyKey parameter on registerProduct; idempotency cannot be set at call-site.
        return catalogDistributorApi.registerProduct(distributorId, cmd)
                .map(result -> (UUID) result);
    }

    @Override
    public Mono<ProductDetailDTO> getProduct(UUID distributorId, UUID productId) {
        log.info("Getting product {} for distributor: {}", productId, distributorId);
        return productsApi.getProductInfo(productId)
                .map(productMapper::toDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> updateProduct(UUID distributorId, UUID productId, UpdateProductRequest request) {
        log.info("Updating product {} for distributor: {}", productId, distributorId);
        UpdateProductCommand cmd = new UpdateProductCommand();
        // ARCH-EXCEPTION: domain-distributor-catalog-sdk generated client does not expose an
        // xIdempotencyKey parameter on reviseProduct; idempotency cannot be set at call-site.
        return catalogDistributorApi.reviseProduct(distributorId, productId, cmd)
                .map(result -> (UUID) result);
    }

    @Override
    public Mono<Void> removeProduct(UUID distributorId, UUID productId) {
        log.info("Removing product {} for distributor: {}", productId, distributorId);
        UpdateProductInfoCommand cmd = new UpdateProductInfoCommand();
        return catalogDistributorApi.retireProduct(distributorId, productId, cmd).then();
    }

    @Override
    public Flux<ProductDetailDTO> listActiveProducts(UUID distributorId) {
        log.info("Listing active products for distributor: {}", distributorId);
        return catalogDistributorApi.listCatalog(distributorId)
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .map(productMapper::fromCatalogDto);
    }
}
