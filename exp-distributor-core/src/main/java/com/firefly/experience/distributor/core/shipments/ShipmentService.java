package com.firefly.experience.distributor.core.shipments;

import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ShipmentService {

    Mono<UUID> shipItem(UUID distributorId, UUID productId, RegisterShipmentRequest request);

    Flux<ShipmentTrackingDTO> trackShipments(UUID distributorId, UUID productId);
}
