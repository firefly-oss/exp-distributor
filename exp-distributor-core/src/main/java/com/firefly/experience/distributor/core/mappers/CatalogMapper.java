package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.catalog.sdk.model.ProductDTO;
import com.firefly.experience.distributor.interfaces.dtos.CatalogItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    @Mapping(target = "productName", source = "name")
    CatalogItemDTO toDto(ProductDTO sdk);
}
