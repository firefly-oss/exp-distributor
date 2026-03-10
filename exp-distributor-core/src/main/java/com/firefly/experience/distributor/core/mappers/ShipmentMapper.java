package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.catalog.sdk.model.RegisterShipmentCommand;
import com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    @Mapping(target = "shipmentId", source = "id")
    @Mapping(target = "currentStatus", source = "status")
    ShipmentTrackingDTO toDto(ShipmentDTO sdk);

    @Mapping(target = "estimatedDeliveryDate", source = "estimatedDelivery")
    @Mapping(target = "productId", ignore = true)
    RegisterShipmentCommand toCommand(RegisterShipmentRequest request);
}
