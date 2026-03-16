package com.firefly.experience.distributor.core.shipments;

import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.api.ShipmentQueriesApi;
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
 * the catalog query SDK ({@code domain-distributor-catalog} ShipmentQueriesApi).
 *
 * <p>{@link #listShipments} fans out across all catalog items for the distributor
 * -- {@code listCatalog} -> per-product {@code trackProductShipments} -- because
 * neither SDK exposes a direct "list shipments by distributorId" endpoint.
 */
@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final DistributorApi catalogDistributorApi;
    private final ShipmentQueriesApi shipmentQueriesApi;
    private final ShipmentMapper shipmentMapper;

    public ShipmentServiceImpl(
            @Qualifier("catalogDistributorApi") DistributorApi catalogDistributorApi,
            ShipmentQueriesApi shipmentQueriesApi,
            ShipmentMapper shipmentMapper) {
        this.catalogDistributorApi = catalogDistributorApi;
        this.shipmentQueriesApi = shipmentQueriesApi;
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
        return catalogDistributorApi.listCatalog(distributorId, null)
                .filter(product -> product.getId() != null)
                .flatMap(product ->
                        catalogDistributorApi.trackProductShipments(distributorId, product.getId(), null))
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCatalogShipmentDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    /**
     * Registers a new shipment via the catalog command SDK, then reads back the
     * full record from the catalog query SDK to return a complete {@link ShipmentDTO}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Mono<ShipmentDTO> registerShipment(UUID distributorId, RegisterShipmentRequest request) {
        log.info("Registering shipment for distributor: {}", distributorId);
        RegisterShipmentCommand cmd = shipmentMapper.toCommand(request);
        cmd.setProductId(request.getProductId());
        return catalogDistributorApi.shipContractItem(distributorId, request.getProductId(), cmd, UUID.randomUUID().toString())
                .flatMap(result -> {
                    UUID shipmentId = (UUID) result;
                    return shipmentQueriesApi.getShipment(shipmentId, null);
                })
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCatalogDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    @Override
    public Mono<ShipmentDTO> getShipment(UUID distributorId, UUID shipmentId) {
        log.info("Getting shipment: {}, distributor: {}", shipmentId, distributorId);
        return shipmentQueriesApi.getShipment(shipmentId, null)
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCatalogDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    @Override
    public Mono<ShipmentDTO> updateShipment(UUID distributorId, UUID shipmentId,
                                             UpdateShipmentRequest request) {
        log.info("Updating shipment: {}, distributor: {}", shipmentId, distributorId);
        com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO updateCmd =
                shipmentMapper.toUpdateSdkDto(request);
        return shipmentQueriesApi.updateShipment(shipmentId, updateCmd, UUID.randomUUID().toString())
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCatalogDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }

    @Override
    public Mono<Void> deleteShipment(UUID distributorId, UUID shipmentId) {
        log.info("Deleting shipment: {}, distributor: {}", shipmentId, distributorId);
        return shipmentQueriesApi.deleteShipment(shipmentId, UUID.randomUUID().toString());
    }

    /**
     * Returns tracking details by reading the shipment from the catalog query SDK.
     * The catalog {@link com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO}
     * carries trackingNumber, carrier, and status sufficient to populate a
     * {@link ShipmentTrackingDTO} without a separate catalog call.
     */
    @Override
    public Mono<ShipmentTrackingDTO> getTracking(UUID distributorId, UUID shipmentId) {
        log.info("Getting tracking for shipment: {}, distributor: {}", shipmentId, distributorId);
        return shipmentQueriesApi.getShipment(shipmentId, null)
                .map(shipmentMapper::toCatalogTracking);
    }

    @Override
    public Mono<ShipmentDTO> updateStatus(UUID distributorId, UUID shipmentId,
                                           UpdateStatusRequest request) {
        log.info("Updating status for shipment: {}, distributor: {}", shipmentId, distributorId);
        return shipmentQueriesApi.updateShipmentStatus(shipmentId, request.getStatus(), null, UUID.randomUUID().toString())
                .map(sdkShipment -> {
                    ShipmentDTO dto = shipmentMapper.toCatalogDto(sdkShipment);
                    dto.setDistributorId(distributorId);
                    return dto;
                });
    }
}
