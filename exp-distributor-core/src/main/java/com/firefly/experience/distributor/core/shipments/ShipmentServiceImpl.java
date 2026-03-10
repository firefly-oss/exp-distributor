package com.firefly.experience.distributor.core.shipments;

import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.model.RegisterShipmentCommand;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import com.firefly.experience.distributor.core.mappers.ShipmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final DistributorApi catalogDistributorApi;
    private final ShipmentMapper shipmentMapper;

    public ShipmentServiceImpl(
            @Qualifier("catalogDistributorApi") DistributorApi catalogDistributorApi,
            ShipmentMapper shipmentMapper) {
        this.catalogDistributorApi = catalogDistributorApi;
        this.shipmentMapper = shipmentMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<UUID> shipItem(UUID distributorId, UUID productId, RegisterShipmentRequest request) {
        log.info("Shipping item for distributor: {}, product: {}", distributorId, productId);
        RegisterShipmentCommand cmd = shipmentMapper.toCommand(request);
        cmd.setProductId(productId);
        return catalogDistributorApi.shipContractItem(distributorId, productId, cmd)
                .map(result -> (UUID) result);
    }

    @Override
    public Flux<ShipmentTrackingDTO> trackShipments(UUID distributorId, UUID productId) {
        log.info("Tracking shipments for distributor: {}, product: {}", distributorId, productId);
        return catalogDistributorApi.trackProductShipments(distributorId, productId)
                .map(shipmentMapper::toDto);
    }
}
