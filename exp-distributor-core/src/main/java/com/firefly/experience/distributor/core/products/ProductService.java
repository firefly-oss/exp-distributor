package com.firefly.experience.distributor.core.products;

import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductService {

    Mono<ProductDetailDTO> getProduct(UUID productId);

    Mono<UUID> registerProduct(RegisterProductRequest request);
}
