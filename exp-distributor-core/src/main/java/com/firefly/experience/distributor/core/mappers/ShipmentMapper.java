package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.catalog.sdk.model.RegisterShipmentCommand;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentDTO;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for shipment-related types across two SDKs.
 *
 * <p>Two downstream SDKs both expose a class named {@code ShipmentDTO}:
 * <ul>
 *   <li>{@code com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO} — catalog (write-side)</li>
 *   <li>{@code com.firefly.core.distributor.sdk.model.ShipmentDTO} — core CRUD (read-side)</li>
 * </ul>
 * Methods are distinguished by fully-qualified parameter types; MapStruct resolves
 * the correct implementation at compile time.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipmentMapper {

    // ── Catalog SDK (write-side) ───────────────────────────────────────────────

    /** Catalog SDK ShipmentDTO → experience ShipmentTrackingDTO (used by legacy trackShipments). */
    @Mapping(target = "shipmentId", source = "id")
    @Mapping(target = "currentStatus", source = "status")
    ShipmentTrackingDTO toDto(com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO sdk);

    /** RegisterShipmentRequest → catalog SDK RegisterShipmentCommand. */
    @Mapping(target = "estimatedDeliveryDate", source = "estimatedDelivery")
    @Mapping(target = "productId", ignore = true)
    RegisterShipmentCommand toCommand(RegisterShipmentRequest request);

    /** Catalog SDK ShipmentDTO → experience ShipmentDTO. */
    @Mapping(target = "estimatedDelivery", source = "estimatedDeliveryDate")
    @Mapping(target = "distributorId", ignore = true)
    ShipmentDTO toCatalogShipmentDto(com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO sdk);

    // ── Core SDK (read/CRUD side) ─────────────────────────────────────────────

    /** Core SDK ShipmentDTO → experience ShipmentDTO. */
    @Mapping(target = "estimatedDelivery", source = "estimatedDeliveryDate")
    @Mapping(target = "distributorId", ignore = true)
    ShipmentDTO toCoreDto(com.firefly.core.distributor.sdk.model.ShipmentDTO sdk);

    /** Core SDK ShipmentDTO → experience ShipmentTrackingDTO (for getTracking). */
    @Mapping(target = "shipmentId", source = "id")
    @Mapping(target = "currentStatus", source = "status")
    ShipmentTrackingDTO toCoreTracking(com.firefly.core.distributor.sdk.model.ShipmentDTO sdk);

    /**
     * UpdateShipmentRequest → core SDK ShipmentDTO (for updateShipment).
     * Only the fields present in the request are mapped; all others default to null.
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "trackingNumber", source = "trackingNumber")
    @Mapping(target = "carrier", source = "carrier")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "estimatedDeliveryDate", source = "estimatedDelivery")
    com.firefly.core.distributor.sdk.model.ShipmentDTO toUpdateSdkDto(UpdateShipmentRequest request);
}
