package com.firefly.experience.distributor.core.shipments;

import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.model.ProductDTO;
import com.firefly.experience.distributor.core.mappers.ShipmentMapper;
import com.firefly.experience.distributor.interfaces.dtos.RegisterShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentDTO;
import com.firefly.experience.distributor.interfaces.dtos.ShipmentTrackingDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateShipmentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateStatusRequest;
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
    private ShipmentApi coreShipmentApi;

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

    private com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO buildCatalogShipment(
            UUID shipmentId, UUID productId) {
        com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO s =
                new com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO();
        s.setId(shipmentId);
        s.setProductId(productId);
        s.setStatus("SHIPPED");
        return s;
    }

    private com.firefly.core.distributor.sdk.model.ShipmentDTO buildCoreShipment(UUID shipmentId) {
        com.firefly.core.distributor.sdk.model.ShipmentDTO s =
                new com.firefly.core.distributor.sdk.model.ShipmentDTO();
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

        com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO sdkShipment1 =
                buildCatalogShipment(shipmentId1, productId1);
        com.firefly.domain.distributor.catalog.sdk.model.ShipmentDTO sdkShipment2 =
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
    void registerShipment_shouldCallCatalogThenReadBackFromCore() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        RegisterShipmentRequest request = RegisterShipmentRequest.builder()
                .productId(productId)
                .trackingNumber("TRK-001")
                .carrier("DHL")
                .build();

        com.firefly.domain.distributor.catalog.sdk.model.RegisterShipmentCommand cmd =
                new com.firefly.domain.distributor.catalog.sdk.model.RegisterShipmentCommand();
        com.firefly.core.distributor.sdk.model.ShipmentDTO coreShipment = buildCoreShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);

        when(shipmentMapper.toCommand(request)).thenReturn(cmd);
        when(catalogDistributorApi.shipContractItem(eq(distributorId), eq(productId), any(), any()))
                .thenReturn(Mono.just(shipmentId));
        when(coreShipmentApi.getShipmentById(eq(shipmentId), any())).thenReturn(Mono.just(coreShipment));
        when(shipmentMapper.toCoreDto(coreShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.registerShipment(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getTrackingNumber()).isEqualTo("TRK-001");
                })
                .verifyComplete();

        verify(catalogDistributorApi).shipContractItem(eq(distributorId), eq(productId), any(), any());
        verify(coreShipmentApi).getShipmentById(eq(shipmentId), any());
    }

    // ── getShipment ──────────────────────────────────────────────────────────

    @Test
    void getShipment_shouldDelegateToCoreAndSetDistributorId() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        com.firefly.core.distributor.sdk.model.ShipmentDTO coreShipment = buildCoreShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);

        when(coreShipmentApi.getShipmentById(eq(shipmentId), any())).thenReturn(Mono.just(coreShipment));
        when(shipmentMapper.toCoreDto(coreShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.getShipment(distributorId, shipmentId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(shipmentId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(coreShipmentApi).getShipmentById(eq(shipmentId), any());
    }

    // ── updateShipment ───────────────────────────────────────────────────────

    @Test
    void updateShipment_shouldMapRequestAndDelegateToCore() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        UpdateShipmentRequest request = UpdateShipmentRequest.builder()
                .trackingNumber("TRK-999")
                .carrier("FedEx")
                .build();

        com.firefly.core.distributor.sdk.model.ShipmentDTO updateCmd =
                new com.firefly.core.distributor.sdk.model.ShipmentDTO();
        com.firefly.core.distributor.sdk.model.ShipmentDTO coreShipment = buildCoreShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);

        when(shipmentMapper.toUpdateSdkDto(request)).thenReturn(updateCmd);
        when(coreShipmentApi.updateShipment(eq(shipmentId), eq(updateCmd), any())).thenReturn(Mono.just(coreShipment));
        when(shipmentMapper.toCoreDto(coreShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.updateShipment(distributorId, shipmentId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(shipmentId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(coreShipmentApi).updateShipment(eq(shipmentId), any(), any());
    }

    // ── deleteShipment ───────────────────────────────────────────────────────

    @Test
    void deleteShipment_shouldDelegateToCoreAndCompleteEmpty() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        when(coreShipmentApi.deleteShipment(eq(shipmentId), any())).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteShipment(distributorId, shipmentId))
                .verifyComplete();

        verify(coreShipmentApi).deleteShipment(eq(shipmentId), any());
    }

    // ── getTracking ──────────────────────────────────────────────────────────

    @Test
    void getTracking_shouldMapCoreShipmentToTrackingDto() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        com.firefly.core.distributor.sdk.model.ShipmentDTO coreShipment = buildCoreShipment(shipmentId);
        ShipmentTrackingDTO trackingDTO = ShipmentTrackingDTO.builder()
                .shipmentId(shipmentId)
                .trackingNumber("TRK-001")
                .carrier("DHL")
                .currentStatus("IN_TRANSIT")
                .build();

        when(coreShipmentApi.getShipmentById(eq(shipmentId), any())).thenReturn(Mono.just(coreShipment));
        when(shipmentMapper.toCoreTracking(coreShipment)).thenReturn(trackingDTO);

        StepVerifier.create(service.getTracking(distributorId, shipmentId))
                .assertNext(dto -> {
                    assertThat(dto.getShipmentId()).isEqualTo(shipmentId);
                    assertThat(dto.getCurrentStatus()).isEqualTo("IN_TRANSIT");
                    assertThat(dto.getCarrier()).isEqualTo("DHL");
                })
                .verifyComplete();

        verify(coreShipmentApi).getShipmentById(eq(shipmentId), any());
    }

    // ── updateStatus ─────────────────────────────────────────────────────────

    @Test
    void updateStatus_shouldCallCoreUpdateStatusAndReturnDto() {
        UUID distributorId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();

        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status("DELIVERED")
                .notes("Delivered to front door")
                .build();

        com.firefly.core.distributor.sdk.model.ShipmentDTO coreShipment = buildCoreShipment(shipmentId);
        ShipmentDTO expectedDto = buildExpShipmentDTO(shipmentId, distributorId);
        expectedDto.setStatus("DELIVERED");

        when(coreShipmentApi.updateShipmentStatus(eq(shipmentId), eq("DELIVERED"), isNull(), any()))
                .thenReturn(Mono.just(coreShipment));
        when(shipmentMapper.toCoreDto(coreShipment)).thenReturn(expectedDto);

        StepVerifier.create(service.updateStatus(distributorId, shipmentId, request))
                .assertNext(dto -> {
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getStatus()).isEqualTo("DELIVERED");
                })
                .verifyComplete();

        verify(coreShipmentApi).updateShipmentStatus(eq(shipmentId), eq("DELIVERED"), isNull(), any());
    }
}
