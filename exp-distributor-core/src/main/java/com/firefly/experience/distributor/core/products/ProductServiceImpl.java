package com.firefly.experience.distributor.core.products;

import com.firefly.domain.product.catalog.sdk.api.ProductsApi;
import com.firefly.domain.product.catalog.sdk.model.RegisterProductCommand;
import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import com.firefly.experience.distributor.core.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductsApi productsApi;
    private final ProductMapper productMapper;

    @Override
    public Mono<ProductDetailDTO> getProduct(UUID productId) {
        log.info("Getting product: {}", productId);
        return productsApi.getProductInfo(productId)
                .map(productMapper::toDto);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> registerProduct(RegisterProductRequest request) {
        log.info("Registering product: {}", request.getName());
        RegisterProductCommand cmd = new RegisterProductCommand();
        return productsApi.registerProduct(cmd)
                .map(result -> (UUID) result);
    }

}
