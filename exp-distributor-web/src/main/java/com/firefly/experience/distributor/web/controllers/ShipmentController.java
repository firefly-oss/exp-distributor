package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.shipments.ShipmentService;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentDTO;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Manage distributor shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List shipments",
               description = "List all shipments for a distributor, aggregated across all catalog products.")
    public Mono<ResponseEntity<List<ShipmentDTO>>> listShipments(
            @PathVariable UUID distributorId) {
        return shipmentService.listShipments(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register shipment",
               description = "Register a new shipment for a distributor product.")
    public Mono<ResponseEntity<ShipmentDTO>> registerShipment(
            @PathVariable UUID distributorId,
            @Valid @RequestBody RegisterShipmentRequest request) {
        return shipmentService.registerShipment(distributorId, request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping(value = "/{shipmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get shipment",
               description = "Retrieve a specific shipment by ID.")
    public Mono<ResponseEntity<ShipmentDTO>> getShipment(
            @PathVariable UUID distributorId,
            @PathVariable UUID shipmentId) {
        return shipmentService.getShipment(distributorId, shipmentId)
                .map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{shipmentId}",
                consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update shipment",
               description = "Update shipment details (carrier, tracking number, address, estimated delivery).")
    public Mono<ResponseEntity<ShipmentDTO>> updateShipment(
            @PathVariable UUID distributorId,
            @PathVariable UUID shipmentId,
            @Valid @RequestBody UpdateShipmentRequest request) {
        return shipmentService.updateShipment(distributorId, shipmentId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{shipmentId}")
    @Operation(summary = "Delete shipment",
               description = "Delete a shipment record.")
    public Mono<ResponseEntity<Void>> deleteShipment(
            @PathVariable UUID distributorId,
            @PathVariable UUID shipmentId) {
        return shipmentService.deleteShipment(distributorId, shipmentId)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }

    @GetMapping(value = "/{shipmentId}/tracking", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get shipment tracking",
               description = "Retrieve real-time tracking information for a shipment.")
    public Mono<ResponseEntity<ShipmentTrackingDTO>> getTracking(
            @PathVariable UUID distributorId,
            @PathVariable UUID shipmentId) {
        return shipmentService.getTracking(distributorId, shipmentId)
                .map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{shipmentId}/status",
                consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update shipment status",
               description = "Update the status of a shipment (e.g. SHIPPED, IN_TRANSIT, DELIVERED).")
    public Mono<ResponseEntity<ShipmentDTO>> updateStatus(
            @PathVariable UUID distributorId,
            @PathVariable UUID shipmentId,
            @Valid @RequestBody UpdateStatusRequest request) {
        return shipmentService.updateStatus(distributorId, shipmentId, request)
                .map(ResponseEntity::ok);
    }
}
