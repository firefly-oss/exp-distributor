package com.firefly.experience.distributor.core.products;

import com.firefly.domain.distributor.catalog.sdk.api.DistributorApi;
import com.firefly.domain.distributor.catalog.sdk.model.ProductDTO;
import com.firefly.domain.product.catalog.sdk.api.ProductsApi;
import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateProductRequest;
import com.firefly.experience.distributor.core.mappers.ProductMapper;
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
class ProductServiceImplTest {

    @Mock
    private DistributorApi catalogDistributorApi;

    @Mock
    private ProductsApi productsApi;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl service;

    private ProductDTO buildCatalogProductDTO(UUID productId, UUID distributorId, boolean active) {
        ProductDTO dto = new ProductDTO();
        dto.setId(productId);
        dto.setDistributorId(distributorId);
        dto.setName("Test Product");
        dto.setDescription("A test product");
        dto.setIsActive(active);
        return dto;
    }

    private ProductDetailDTO buildProductDetailDTO(UUID productId, UUID distributorId) {
        return ProductDetailDTO.builder()
                .id(productId)
                .distributorId(distributorId)
                .name("Test Product")
                .description("A test product")
                .isActive(true)
                .build();
    }

    @Test
    void listProducts_shouldReturnAllProductsMappedFromCatalog() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ProductDTO sdkProduct = buildCatalogProductDTO(productId, distributorId, true);
        ProductDetailDTO expected = buildProductDetailDTO(productId, distributorId);

        when(catalogDistributorApi.listCatalog(distributorId)).thenReturn(Flux.just(sdkProduct));
        when(productMapper.fromCatalogDto(sdkProduct)).thenReturn(expected);

        StepVerifier.create(service.listProducts(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(productId);
                    assertThat(dto.getName()).isEqualTo("Test Product");
                })
                .verifyComplete();

        verify(catalogDistributorApi).listCatalog(distributorId);
    }

    @Test
    void registerProduct_shouldCallCatalogApiAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID newId = UUID.randomUUID();
        RegisterProductRequest request = RegisterProductRequest.builder()
                .name("New Product")
                .category("Electronics")
                .build();

        when(catalogDistributorApi.registerProduct(eq(distributorId), any()))
                .thenReturn(Mono.just(newId));

        StepVerifier.create(service.registerProduct(distributorId, request))
                .assertNext(id -> assertThat(id).isEqualTo(newId))
                .verifyComplete();

        verify(catalogDistributorApi).registerProduct(eq(distributorId), any());
    }

    @Test
    void getProduct_shouldCallProductsApiByIdAndReturnMappedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        com.firefly.domain.product.catalog.sdk.model.ProductDTO sdkDto =
                new com.firefly.domain.product.catalog.sdk.model.ProductDTO();
        ProductDetailDTO expected = buildProductDetailDTO(productId, distributorId);

        when(productsApi.getProductInfo(productId)).thenReturn(Mono.just(sdkDto));
        when(productMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.getProduct(distributorId, productId))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(productId))
                .verifyComplete();

        verify(productsApi).getProductInfo(productId);
    }

    @Test
    void updateProduct_shouldCallReviseProductAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("Updated Name")
                .isActive(true)
                .build();

        when(catalogDistributorApi.reviseProduct(eq(distributorId), eq(productId), any()))
                .thenReturn(Mono.just(productId));

        StepVerifier.create(service.updateProduct(distributorId, productId, request))
                .assertNext(id -> assertThat(id).isEqualTo(productId))
                .verifyComplete();

        verify(catalogDistributorApi).reviseProduct(eq(distributorId), eq(productId), any());
    }

    @Test
    void removeProduct_shouldCallRetireProductAndCompleteEmpty() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(catalogDistributorApi.retireProduct(eq(distributorId), eq(productId), any()))
                .thenReturn(Mono.just(productId));

        StepVerifier.create(service.removeProduct(distributorId, productId))
                .verifyComplete();

        verify(catalogDistributorApi).retireProduct(eq(distributorId), eq(productId), any());
    }

    @Test
    void listActiveProducts_shouldOnlyReturnActiveItems() {
        UUID distributorId = UUID.randomUUID();
        UUID activeId = UUID.randomUUID();
        UUID inactiveId = UUID.randomUUID();

        ProductDTO active = buildCatalogProductDTO(activeId, distributorId, true);
        ProductDTO inactive = buildCatalogProductDTO(inactiveId, distributorId, false);
        ProductDetailDTO expectedDto = buildProductDetailDTO(activeId, distributorId);

        when(catalogDistributorApi.listCatalog(distributorId)).thenReturn(Flux.just(active, inactive));
        when(productMapper.fromCatalogDto(active)).thenReturn(expectedDto);

        StepVerifier.create(service.listActiveProducts(distributorId))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(activeId))
                .verifyComplete();
    }
}
