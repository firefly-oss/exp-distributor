package com.firefly.experience.distributor.core.shipments;

import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.model.RegisterShipmentCommand;
import com.firefly.experience.distributor.core.mappers.ShipmentMapper;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentDTO;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateStatusRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Stateless composition service for distributor shipment management.
 *
 * <p>Write operations (register) route to the catalog command SDK
 * ({@code domain-distributor-catalog}), while read/CRUD operations route to
 * the core query SDK ({@code core-common-distributor-mgmt}).
 *
 * <p>{@link #listShipments} fans out across all catalog items for the distributor
 * — {@code listCatalog} → per-product {@code trackProductShipments} — because
 * neither SDK exposes a direct "list shipments by distributorId" endpoint.
 */
@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final DistributorApi catalogDistributorApi;
    private final ShipmentApi coreShipmentApi;
    private final ShipmentMapper shipmentMapper;

    public ShipmentServiceImpl(
            @Qualifier("catalogDistributorApi") DistributorApi catalogDistributorApi,
            @Qualifier("coreShipmentApi") ShipmentApi coreShipmentApi,
            ShipmentMapper shipmentMapper) {
        this.catalogDistributorApi = catalogDistributorApi;
        this.coreShipmentApi = coreShipmentApi;
        this.shipmentMapper = shipmentMapper;
    }

    /**
     * Lists all shipments for a distributor by fanning out across catalog products.
     * Each product's shipments are fetched via the catalog SDK and mapped to the
     * experience-layer DTO, with the distributorId injected post-mapping.
     */
    @Override
    public Flux<ShipmentDTO> listShipments(UUID distributorId) {
        log.info("Listing shipments for distributor: {}", distributorId);
        return catalogDistributorApi.listCatalog(distributorId)
                .filter(product -> product.getId() != null)
                .flatMap(product ->
                        catalogDistributorApi.trackProductShipments(distributorId, product.getId()))
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCatalogShipmentDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    /**
     * Registers a new shipment via the catalog command SDK, then reads back the
     * full record from the core CRUD SDK to return a complete {@link ShipmentDTO}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Mono<ShipmentDTO> registerShipment(UUID distributorId, RegisterShipmentRequest request) {
        log.info("Registering shipment for distributor: {}", distributorId);
        RegisterShipmentCommand cmd = shipmentMapper.toCommand(request);
        cmd.setProductId(request.getProductId());
        // ARCH-EXCEPTION: domain-distributor-catalog-sdk generated client does not expose an
        // xIdempotencyKey parameter on shipContractItem; idempotency cannot be set at call-site.
        return catalogDistributorApi.shipContractItem(distributorId, request.getProductId(), cmd)
                .flatMap(result -> {
                    UUID shipmentId = (UUID) result;
                    return coreShipmentApi.getShipmentById(shipmentId);
                })
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCoreDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    @Override
    public Mono<ShipmentDTO> getShipment(UUID distributorId, UUID shipmentId) {
        log.info("Getting shipment: {}, distributor: {}", shipmentId, distributorId);
        return coreShipmentApi.getShipmentById(shipmentId)
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCoreDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    @Override
    public Mono<ShipmentDTO> updateShipment(UUID distributorId, UUID shipmentId,
                                             UpdateShipmentRequest request) {
        log.info("Updating shipment: {}, distributor: {}", shipmentId, distributorId);
        com.firefly.core.distributor.sdk.model.ShipmentDTO updateCmd =
                shipmentMapper.toUpdateSdkDto(request);
        // ARCH-EXCEPTION: core-common-distributor-mgmt-sdk generated client does not expose an
        // xIdempotencyKey parameter on updateShipment; idempotency cannot be set at call-site.
        return coreShipmentApi.updateShipment(shipmentId, updateCmd)
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCoreDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    @Override
    public Mono<Void> deleteShipment(UUID distributorId, UUID shipmentId) {
        log.info("Deleting shipment: {}, distributor: {}", shipmentId, distributorId);
        return coreShipmentApi.deleteShipment(shipmentId);
    }

    /**
     * Returns tracking details by reading the shipment from the core SDK.
     * The core {@link ShipmentDTO} carries trackingNumber, carrier, and status
     * sufficient to populate a {@link ShipmentTrackingDTO} without a separate
     * catalog call.
     */
    @Override
    public Mono<ShipmentTrackingDTO> getTracking(UUID distributorId, UUID shipmentId) {
        log.info("Getting tracking for shipment: {}, distributor: {}", shipmentId, distributorId);
        return coreShipmentApi.getShipmentById(shipmentId)
                .map(shipmentMapper::toCoreTracking);
    }

    @Override
    public Mono<ShipmentDTO> updateStatus(UUID distributorId, UUID shipmentId,
                                           UpdateStatusRequest request) {
        log.info("Updating status for shipment: {}, distributor: {}", shipmentId, distributorId);
        // ARCH-EXCEPTION: core-common-distributor-mgmt-sdk generated client does not expose an
        // xIdempotencyKey parameter on updateShipmentStatus; idempotency cannot be set at call-site.
        return coreShipmentApi.updateShipmentStatus(shipmentId, request.getStatus(), null)
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCoreDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }
}
