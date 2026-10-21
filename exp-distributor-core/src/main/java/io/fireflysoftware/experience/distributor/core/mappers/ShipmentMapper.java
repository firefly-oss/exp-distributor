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

import io.fireflysoftware.domain.distributor.catalog.sdk.model.RegisterShipmentCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ShipmentDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for shipment-related types.
 *
 * <p>All downstream shipment types come from the catalog SDK:
 * {@code io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipmentMapper {

    // ── Catalog SDK ──────────────────────────────────────────────────────────

    /** Catalog SDK ShipmentDTO -> experience ShipmentTrackingDTO (used by legacy trackShipments). */
    @Mapping(target = "shipmentId", source = "id")
    @Mapping(target = "currentStatus", source = "status")
    ShipmentTrackingDTO toDto(io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO sdk);

    /** RegisterShipmentRequest -> catalog SDK RegisterShipmentCommand. */
    @Mapping(target = "estimatedDeliveryDate", source = "estimatedDelivery")
    @Mapping(target = "productId", ignore = true)
    RegisterShipmentCommand toCommand(RegisterShipmentRequest request);

    /** Catalog SDK ShipmentDTO -> experience ShipmentDTO. */
    @Mapping(target = "estimatedDelivery", source = "estimatedDeliveryDate")
    @Mapping(target = "distributorId", ignore = true)
    ShipmentDTO toCatalogShipmentDto(io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO sdk);

    /** Catalog SDK ShipmentDTO -> experience ShipmentDTO (for query reads). */
    @Mapping(target = "estimatedDelivery", source = "estimatedDeliveryDate")
    @Mapping(target = "distributorId", ignore = true)
    ShipmentDTO toCatalogDto(io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO sdk);

    /** Catalog SDK ShipmentDTO -> experience ShipmentTrackingDTO (for getTracking). */
    @Mapping(target = "shipmentId", source = "id")
    @Mapping(target = "currentStatus", source = "status")
    ShipmentTrackingDTO toCatalogTracking(io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO sdk);

    /**
     * UpdateShipmentRequest -> catalog SDK ShipmentDTO (for updateShipment).
     * Only the fields present in the request are mapped; all others default to null.
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "trackingNumber", source = "trackingNumber")
    @Mapping(target = "carrier", source = "carrier")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "estimatedDeliveryDate", source = "estimatedDelivery")
    io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO toUpdateSdkDto(UpdateShipmentRequest request);
}
