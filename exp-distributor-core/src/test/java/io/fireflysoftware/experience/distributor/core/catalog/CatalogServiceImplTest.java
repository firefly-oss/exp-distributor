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


package io.fireflysoftware.experience.distributor.core.catalog;

import io.fireflysoftware.domain.distributor.catalog.sdk.api.DistributorApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.model.ProductDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AddCatalogItemRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CatalogItemDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateCatalogItemRequest;
import io.fireflysoftware.experience.distributor.core.mappers.CatalogMapper;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock
    private DistributorApi catalogDistributorApi;

    @Mock
    private CatalogMapper catalogMapper;

    @InjectMocks
    private CatalogServiceImpl service;

    private ProductDTO buildSdkProductDTO(UUID productId, UUID distributorId) {
        ProductDTO dto = new ProductDTO();
        dto.setId(productId);
        dto.setDistributorId(distributorId);
        dto.setName("Test Product");
        dto.setIsActive(true);
        return dto;
    }

    private CatalogItemDTO buildCatalogItemDTO(UUID productId, UUID distributorId) {
        return CatalogItemDTO.builder()
                .id(productId)
                .distributorId(distributorId)
                .productName("Test Product")
                .isActive(true)
                .build();
    }

    @Test
    void listCatalog_shouldReturnMappedItems() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ProductDTO sdkProduct = buildSdkProductDTO(productId, distributorId);
        CatalogItemDTO expectedDto = buildCatalogItemDTO(productId, distributorId);

        when(catalogDistributorApi.listCatalog(eq(distributorId), any())).thenReturn(Flux.just(sdkProduct));
        when(catalogMapper.toDto(sdkProduct)).thenReturn(expectedDto);

        StepVerifier.create(service.listCatalog(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(productId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getProductName()).isEqualTo("Test Product");
                })
                .verifyComplete();

        verify(catalogDistributorApi).listCatalog(eq(distributorId), any());
    }

    @Test
    void addToCatalog_shouldCallRegisterProductAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID newId = UUID.randomUUID();
        AddCatalogItemRequest request = AddCatalogItemRequest.builder()
                .productId(UUID.randomUUID())
                .productName("New Product")
                .build();

        when(catalogDistributorApi.registerProduct(eq(distributorId), any(), any()))
                .thenReturn(Mono.just(newId));

        StepVerifier.create(service.addToCatalog(distributorId, request))
                .assertNext(id -> assertThat(id).isEqualTo(newId))
                .verifyComplete();

        verify(catalogDistributorApi).registerProduct(eq(distributorId), any(), any());
    }

    @Test
    void getCatalogItem_shouldFilterListAndReturnMatchingItem() {
        UUID distributorId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        ProductDTO targetProduct = buildSdkProductDTO(targetId, distributorId);
        ProductDTO otherProduct = buildSdkProductDTO(otherId, distributorId);
        CatalogItemDTO expectedDto = buildCatalogItemDTO(targetId, distributorId);

        when(catalogDistributorApi.listCatalog(eq(distributorId), any()))
                .thenReturn(Flux.just(otherProduct, targetProduct));
        when(catalogMapper.toDto(targetProduct)).thenReturn(expectedDto);

        StepVerifier.create(service.getCatalogItem(distributorId, targetId))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(targetId))
                .verifyComplete();
    }

    @Test
    void getCatalogItem_shouldReturnEmptyWhenNotFound() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID unknownId = UUID.randomUUID();

        ProductDTO sdkProduct = buildSdkProductDTO(productId, distributorId);

        when(catalogDistributorApi.listCatalog(eq(distributorId), any())).thenReturn(Flux.just(sdkProduct));

        StepVerifier.create(service.getCatalogItem(distributorId, unknownId))
                .verifyComplete();
    }

    @Test
    void updateCatalogItem_shouldCallReviseProductAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID catalogItemId = UUID.randomUUID();
        UpdateCatalogItemRequest request = UpdateCatalogItemRequest.builder()
                .productName("Updated Name")
                .isActive(true)
                .build();

        when(catalogDistributorApi.reviseProduct(eq(distributorId), eq(catalogItemId), any(), any()))
                .thenReturn(Mono.just(catalogItemId));

        StepVerifier.create(service.updateCatalogItem(distributorId, catalogItemId, request))
                .assertNext(id -> assertThat(id).isEqualTo(catalogItemId))
                .verifyComplete();

        verify(catalogDistributorApi).reviseProduct(eq(distributorId), eq(catalogItemId), any(), any());
    }

    @Test
    void removeFromCatalog_shouldCallRetireProductAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID catalogItemId = UUID.randomUUID();

        when(catalogDistributorApi.retireProduct(eq(distributorId), eq(catalogItemId), any(), any()))
                .thenReturn(Mono.just(catalogItemId));

        StepVerifier.create(service.removeFromCatalog(distributorId, catalogItemId))
                .assertNext(id -> assertThat(id).isEqualTo(catalogItemId))
                .verifyComplete();

        verify(catalogDistributorApi).retireProduct(eq(distributorId), eq(catalogItemId), any(), any());
    }
}
