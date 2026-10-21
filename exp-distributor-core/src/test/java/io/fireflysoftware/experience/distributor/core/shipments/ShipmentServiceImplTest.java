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


package io.fireflysoftware.experience.distributor.core.shipments;

import io.fireflysoftware.domain.distributor.catalog.sdk.api.DistributorApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.api.ShipmentQueriesApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.model.ProductDTO;
import io.fireflysoftware.experience.distributor.core.mappers.ShipmentMapper;
import io.fireflysoftware.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ShipmentDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateStatusRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    @Mock
    private DistributorApi catalogDistributorApi;

    @Mock
    private ShipmentQueriesApi shipmentQueriesApi;

    @Mock
    private ShipmentMapper shipmentMapper;

    @InjectMocks
    private ShipmentServiceImpl service;

    // ── helpers ───────────────────────────────────────────────────────────────

    private ProductDTO buildProduct(UUID productId) {
        ProductDTO p = new ProductDTO();
        p.setId(productId);
        return p;
    }

    private io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO buildCatalogShipment(
            UUID shipmentId, UUID productId) {
        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO s =
                new io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO();
        s.setId(shipmentId);
        s.setProductId(productId);
        s.setStatus("SHIPPED");
        return s;
    }

    private io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO buildDomainShipment(UUID shipmentId) {
        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO s =
                new io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO();
        s.setId(shipmentId);
        s.setTrackingNumber("TRK-001");
        s.setCarrier("DHL");
        s.setStatus("IN_TRANSIT");
        return s;
    }

    private ShipmentDTO buildExpShipmentDTO(UUID shipmentId, UUID distributorId) {
        return ShipmentDTO.builder()
                .id(shipmentId)
                .distributorId(distributorId)
                .trackingNumber("TRK-001")
                .carrier("DHL")
                .status("IN_TRANSIT")
                .build();
    }

    // ── listShipments ─────────────────────────────────────────────────────────

    @Test
    void listShipments_shouldFanOutAcrossCatalogProducts() {
        UUID distributorId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID shipmentId1 = UUID.randomUUID();
        UUID shipmentId2 = UUID.randomUUID();

        ProductDTO product1 = buildProduct(productId1);
        ProductDTO product2 = buildProduct(productId2);

        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO sdkShipment1 =
                buildCatalogShipment(shipmentId1, productId1);
        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO sdkShipment2 =
                buildCatalogShipment(shipmentId2, productId2);

        ShipmentDTO dto1 = ShipmentDTO.builder().id(shipmentId1).productId(productId1).build();
        ShipmentDTO dto2 = ShipmentDTO.builder().id(shipmentId2).productId(productId2).build();

        when(catalogDistributorApi.listCatalog(eq(distributorId), any()))
                .thenReturn(Flux.just(product1, product2));
        when(catalogDistributorApi.trackProductShipments(eq(distributorId), eq(productId1), any()))
                .thenReturn(Flux.just(sdkShipment1));
        when(catalogDistributorApi.trackProductShipments(eq(distributorId), eq(productId2), any()))
                .thenReturn(Flux.just(sdkShipment2));
        when(shipmentMapper.toCatalogShipmentDto(sdkShipment1)).thenReturn(dto1);
        when(shipmentMapper.toCatalogShipmentDto(sdkShipment2)).thenReturn(dto2);

        StepVerifier.create(service.listShipments(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getId()).isEqualTo(shipmentId1);
                })
                .assertNext(dto -> {
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getId()).isEqualTo(shipmentId2);
                })
                .verifyComplete();

        verify(catalogDistributorApi).trackProductShipments(eq(distributorId), eq(productId1), any());
        verify(catalogDistributorApi).trackProductShipments(eq(distributorId), eq(productId2), any());
    }

    @Test
    void listShipments_shouldSkipProductsWithNullId() {
        UUID distributorId = UUID.randomUUID();
        ProductDTO nullIdProduct = buildProduct(null);

        when(catalogDistributorApi.listCatalog(eq(distributorId), any()))
                .thenReturn(Flux.just(nullIdProduct));

        StepVerifier.create(service.listShipments(distributorId))
                .verifyComplete();
    }

    // ── registerShipment ─────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void registerShipment_shouldCallCatalogThenReadBackFromQuery() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        RegisterShipmentRequest request = RegisterShipmentRequest.builder()
                .productId(productId)
                .trackingNumber("TRK-001")
                .carrier("DHL")
                .build();

        io.fireflysoftware.domain.distributor.catalog.sdk.model.RegisterShipmentCommand cmd =
                new io.fireflysoftware.domain.distributor.catalog.sdk.model.RegisterShipmentCommand();
        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO domainShipment = buildDomainShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);

        when(shipmentMapper.toCommand(request)).thenReturn(cmd);
        when(catalogDistributorApi.shipContractItem(eq(distributorId), eq(productId), any(), any()))
                .thenReturn(Mono.just(shipmentId));
        when(shipmentQueriesApi.getShipment(eq(shipmentId), any())).thenReturn(Mono.just(domainShipment));
        when(shipmentMapper.toCatalogDto(domainShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.registerShipment(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getTrackingNumber()).isEqualTo("TRK-001");
                })
                .verifyComplete();

        verify(catalogDistributorApi).shipContractItem(eq(distributorId), eq(productId), any(), any());
        verify(shipmentQueriesApi).getShipment(eq(shipmentId), any());
    }

    // ── getShipment ──────────────────────────────────────────────────────────

    @Test
    void getShipment_shouldDelegateToQueryApiAndSetDistributorId() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO domainShipment = buildDomainShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);

        when(shipmentQueriesApi.getShipment(eq(shipmentId), any())).thenReturn(Mono.just(domainShipment));
        when(shipmentMapper.toCatalogDto(domainShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.getShipment(distributorId, shipmentId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(shipmentId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(shipmentQueriesApi).getShipment(eq(shipmentId), any());
    }

    // ── updateShipment ───────────────────────────────────────────────────────

    @Test
    void updateShipment_shouldMapRequestAndDelegateToQueryApi() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        UpdateShipmentRequest request = UpdateShipmentRequest.builder()
                .trackingNumber("TRK-999")
                .carrier("FedEx")
                .build();

        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO updateCmd =
                new io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO();
        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO domainShipment = buildDomainShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);

        when(shipmentMapper.toUpdateSdkDto(request)).thenReturn(updateCmd);
        when(shipmentQueriesApi.updateShipment(eq(shipmentId), eq(updateCmd), any())).thenReturn(Mono.just(domainShipment));
        when(shipmentMapper.toCatalogDto(domainShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.updateShipment(distributorId, shipmentId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(shipmentId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(shipmentQueriesApi).updateShipment(eq(shipmentId), any(), any());
    }

    // ── deleteShipment ───────────────────────────────────────────────────────

    @Test
    void deleteShipment_shouldDelegateToQueryApiAndCompleteEmpty() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        when(shipmentQueriesApi.deleteShipment(eq(shipmentId), any())).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteShipment(distributorId, shipmentId))
                .verifyComplete();

        verify(shipmentQueriesApi).deleteShipment(eq(shipmentId), any());
    }

    // ── getTracking ──────────────────────────────────────────────────────────

    @Test
    void getTracking_shouldMapDomainShipmentToTrackingDto() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO domainShipment = buildDomainShipment(shipmentId);
        ShipmentTrackingDTO trackingDTO = ShipmentTrackingDTO.builder()
                .shipmentId(shipmentId)
                .trackingNumber("TRK-001")
                .carrier("DHL")
                .currentStatus("IN_TRANSIT")
                .build();

        when(shipmentQueriesApi.getShipment(eq(shipmentId), any())).thenReturn(Mono.just(domainShipment));
        when(shipmentMapper.toCatalogTracking(domainShipment)).thenReturn(trackingDTO);

        StepVerifier.create(service.getTracking(distributorId, shipmentId))
                .assertNext(dto -> {
                    assertThat(dto.getShipmentId()).isEqualTo(shipmentId);
                    assertThat(dto.getCurrentStatus()).isEqualTo("IN_TRANSIT");
                    assertThat(dto.getCarrier()).isEqualTo("DHL");
                })
                .verifyComplete();

        verify(shipmentQueriesApi).getShipment(eq(shipmentId), any());
    }

    // ── updateStatus ─────────────────────────────────────────────────────────

    @Test
    void updateStatus_shouldCallQueryApiUpdateStatusAndReturnDto() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status("DELIVERED")
                .notes("Delivered to front door")
                .build();

        io.fireflysoftware.domain.distributor.catalog.sdk.model.ShipmentDTO domainShipment = buildDomainShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);
        expectedDto.setStatus("DELIVERED");

        when(shipmentQueriesApi.updateShipmentStatus(eq(shipmentId), eq("DELIVERED"), isNull(), any()))
                .thenReturn(Mono.just(domainShipment));
        when(shipmentMapper.toCatalogDto(domainShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.updateStatus(distributorId, shipmentId, request))
                .assertNext(dto -> {
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getStatus()).isEqualTo("DELIVERED");
                })
                .verifyComplete();

        verify(shipmentQueriesApi).updateShipmentStatus(eq(shipmentId), eq("DELIVERED"), isNull(), any());
    }
}
