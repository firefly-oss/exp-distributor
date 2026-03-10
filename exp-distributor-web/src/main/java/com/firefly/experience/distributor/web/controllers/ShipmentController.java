package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.shipments.ShipmentService;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/products/{productId}/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Manage distributor shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @Operation(summary = "Ship item", description = "Ship a contract item for a product")
    public Mono<ResponseEntity<UUID>> shipItem(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId,
            @Valid @RequestBody RegisterShipmentRequest request) {
        return shipmentService.shipItem(distributorId, productId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @GetMapping
    @Operation(summary = "Track shipments", description = "Track all shipments for a product")
    public Mono<ResponseEntity<List<ShipmentTrackingDTO>>> trackShipments(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId) {
        return shipmentService.trackShipments(distributorId, productId)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
