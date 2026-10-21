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


package io.fireflysoftware.experience.distributor.core.products;

import io.fireflysoftware.experience.distributor.interfaces.dtos.ProductDetailDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.RegisterProductRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateProductRequest;
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
