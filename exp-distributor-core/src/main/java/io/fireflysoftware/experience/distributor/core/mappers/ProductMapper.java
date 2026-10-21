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


package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.product.catalog.sdk.model.ProductDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ProductDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", source = "productId")
    @Mapping(target = "name", source = "productName")
    @Mapping(target = "description", source = "productDescription")
    ProductDetailDTO toDto(ProductDTO sdk);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    ProductDetailDTO fromCatalogDto(io.fireflysoftware.domain.distributor.catalog.sdk.model.ProductDTO sdk);
}
