package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.product.catalog.sdk.model.ProductDTO;
import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
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
    ProductDetailDTO fromCatalogDto(com.firefly.domain.distributor.catalog.sdk.model.ProductDTO sdk);
}
