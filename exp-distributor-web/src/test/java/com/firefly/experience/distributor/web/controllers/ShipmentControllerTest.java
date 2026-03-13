package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.shipments.ShipmentService;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentDTO;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateStatusRequest;
import org.fireflyframework.web.error.config.ErrorHandlingProperties;
import org.fireflyframework.web.error.converter.ExceptionConverterService;
import org.fireflyframework.web.error.service.ErrorResponseNegotiator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ShipmentController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class ShipmentControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ShipmentService shipmentService;

    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH =
            "/api/v1/experience/distributors/{distributorId}/shipments";

    private ShipmentDTO buildShipmentDTO(UUID shipmentId, UUID distributorId) {
        return ShipmentDTO.builder()
                .id(shipmentId)
                .distributorId(distributorId)
                .trackingNumber("TRK-001")
                .carrier("DHL")
                .status("SHIPPED")
                .build();
    }

    // ── GET / ─────────────────────────────────────────────────────────────────

    @Test
    void GET_listShipments_shouldReturn200WithList() {
        UUID distributorId = UUID.randomUUID();
        ShipmentDTO item = buildShipmentDTO(UUID.randomUUID(), distributorId);

        when(shipmentService.listShipments(distributorId)).thenReturn(Flux.just(item));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ShipmentDTO.class)
                .hasSize(1);

        verify(shipmentService).listShipments(distributorId);
    }

    @Test
    void GET_listShipments_shouldReturn200WithEmptyList() {
        UUID distributorId = UUID.randomUUID();

        when(shipmentService.listShipments(distributorId)).thenReturn(Flux.empty());

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ShipmentDTO.class)
                .hasSize(0);
    }

    // ── POST / ────────────────────────────────────────────────────────────────

    @Test
    void POST_registerShipment_shouldReturn201WithDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();
        ShipmentDTO created = buildShipmentDTO(shipmentId, distributorId);

        when(shipmentService.registerShipment(eq(distributorId), any(RegisterShipmentRequest.class)))
                .thenReturn(Mono.just(created));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"productId":"00000000-0000-0000-0000-000000000001","carrier":"DHL"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ShipmentDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(shipmentId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                });

        verify(shipmentService).registerShipment(eq(distributorId), any());
    }

    // ── GET /{shipmentId} ─────────────────────────────────────────────────────

    @Test
    void GET_getShipment_shouldReturn200WithDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();
        ShipmentDTO dto = buildShipmentDTO(shipmentId, distributorId);

        when(shipmentService.getShipment(distributorId, shipmentId)).thenReturn(Mono.just(dto));

        webClient.get()
                .uri(BASE_PATH + "/{shipmentId}", distributorId, shipmentId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShipmentDTO.class)
                .value(result -> assertThat(result.getId()).isEqualTo(shipmentId));

        verify(shipmentService).getShipment(distributorId, shipmentId);
    }

    // ── PUT /{shipmentId} ─────────────────────────────────────────────────────

    @Test
    void PUT_updateShipment_shouldReturn200WithUpdatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();
        ShipmentDTO updated = buildShipmentDTO(shipmentId, distributorId);
        updated.setCarrier("FedEx");

        when(shipmentService.updateShipment(eq(distributorId), eq(shipmentId),
                any(UpdateShipmentRequest.class)))
                .thenReturn(Mono.just(updated));

        webClient.put()
                .uri(BASE_PATH + "/{shipmentId}", distributorId, shipmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"carrier":"FedEx","trackingNumber":"TRK-999"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShipmentDTO.class)
                .value(dto -> assertThat(dto.getCarrier()).isEqualTo("FedEx"));

        verify(shipmentService).updateShipment(eq(distributorId), eq(shipmentId), any());
    }

    // ── DELETE /{shipmentId} ──────────────────────────────────────────────────

    @Test
    void DELETE_deleteShipment_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        when(shipmentService.deleteShipment(distributorId, shipmentId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{shipmentId}", distributorId, shipmentId)
                .exchange()
                .expectStatus().isNoContent();

        verify(shipmentService).deleteShipment(distributorId, shipmentId);
    }

    // ── GET /{shipmentId}/tracking ────────────────────────────────────────────

    @Test
    void GET_getTracking_shouldReturn200WithTrackingDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();
        ShipmentTrackingDTO tracking = ShipmentTrackingDTO.builder()
                .shipmentId(shipmentId)
                .trackingNumber("TRK-001")
                .carrier("DHL")
                .currentStatus("IN_TRANSIT")
                .build();

        when(shipmentService.getTracking(distributorId, shipmentId)).thenReturn(Mono.just(tracking));

        webClient.get()
                .uri(BASE_PATH + "/{shipmentId}/tracking", distributorId, shipmentId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShipmentTrackingDTO.class)
                .value(dto -> {
                    assertThat(dto.getShipmentId()).isEqualTo(shipmentId);
                    assertThat(dto.getCurrentStatus()).isEqualTo("IN_TRANSIT");
                });

        verify(shipmentService).getTracking(distributorId, shipmentId);
    }

    // ── PUT /{shipmentId}/status ──────────────────────────────────────────────

    @Test
    void PUT_updateStatus_shouldReturn200WithUpdatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();
        ShipmentDTO updated = buildShipmentDTO(shipmentId, distributorId);
        updated.setStatus("DELIVERED");

        when(shipmentService.updateStatus(eq(distributorId), eq(shipmentId),
                any(UpdateStatusRequest.class)))
                .thenReturn(Mono.just(updated));

        webClient.put()
                .uri(BASE_PATH + "/{shipmentId}/status", distributorId, shipmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"status":"DELIVERED","notes":"Left at front door"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShipmentDTO.class)
                .value(dto -> assertThat(dto.getStatus()).isEqualTo("DELIVERED"));

        verify(shipmentService).updateStatus(eq(distributorId), eq(shipmentId), any());
    }
}
