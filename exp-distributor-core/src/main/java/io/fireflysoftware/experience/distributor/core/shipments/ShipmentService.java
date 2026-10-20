package io.fireflysoftware.experience.distributor.core.shipments;

import io.fireflysoftware.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ShipmentDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateStatusRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ShipmentService {

    Flux<ShipmentDTO> listShipments(UUID distributorId);

    Mono<ShipmentDTO> registerShipment(UUID distributorId, RegisterShipmentRequest request);

    Mono<ShipmentDTO> getShipment(UUID distributorId, UUID shipmentId);

    Mono<ShipmentDTO> updateShipment(UUID distributorId, UUID shipmentId, UpdateShipmentRequest request);

    Mono<Void> deleteShipment(UUID distributorId, UUID shipmentId);

    Mono<ShipmentTrackingDTO> getTracking(UUID distributorId, UUID shipmentId);

    Mono<ShipmentDTO> updateStatus(UUID distributorId, UUID shipmentId, UpdateStatusRequest request);
}
