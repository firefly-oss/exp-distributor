package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.catalog.sdk.model.ProductDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CatalogItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    @Mapping(target = "productName", source = "name")
    CatalogItemDTO toDto(ProductDTO sdk);
}
