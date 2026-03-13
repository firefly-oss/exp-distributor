package com.firefly.experience.distributor.core.products;

import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateProductRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductService {

    Flux<ProductDetailDTO> listProducts(UUID distributorId);

    Mono<UUID> registerProduct(UUID distributorId, RegisterProductRequest request);

    Mono<ProductDetailDTO> getProduct(UUID distributorId, UUID productId);

    Mono<UUID> updateProduct(UUID distributorId, UUID productId, UpdateProductRequest request);

    Mono<Void> removeProduct(UUID distributorId, UUID productId);

    Flux<ProductDetailDTO> listActiveProducts(UUID distributorId);
}
