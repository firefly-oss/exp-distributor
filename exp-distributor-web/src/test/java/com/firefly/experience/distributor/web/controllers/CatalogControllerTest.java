package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.catalog.CatalogService;
import com.firefly.experience.distributor.interfaces.dtos.AddCatalogItemRequest;
import com.firefly.experience.distributor.interfaces.dtos.CatalogItemDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateCatalogItemRequest;
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

@WebFluxTest(controllers = CatalogController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class CatalogControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/catalog";

    private CatalogItemDTO buildCatalogItemDTO(UUID distributorId, UUID productId) {
        return CatalogItemDTO.builder()
                .id(productId)
                .distributorId(distributorId)
                .productName("Test Product")
                .isActive(true)
                .build();
    }

    @Test
    void GET_listCatalog_shouldReturn200WithList() {
        UUID distributorId = UUID.randomUUID();
        CatalogItemDTO item = buildCatalogItemDTO(distributorId, UUID.randomUUID());
        when(catalogService.listCatalog(distributorId)).thenReturn(Flux.just(item));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CatalogItemDTO.class)
                .hasSize(1);
    }

    @Test
    void POST_addToCatalog_shouldReturn201WithId() {
        UUID distributorId = UUID.randomUUID();
        UUID newId = UUID.randomUUID();

        when(catalogService.addToCatalog(eq(distributorId), any(AddCatalogItemRequest.class)))
                .thenReturn(Mono.just(newId));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"productId":"00000000-0000-0000-0000-000000000001","productName":"Test"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(newId));
    }

    @Test
    void GET_getCatalogItem_shouldReturn200WithDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID catalogItemId = UUID.randomUUID();
        CatalogItemDTO item = buildCatalogItemDTO(distributorId, catalogItemId);

        when(catalogService.getCatalogItem(distributorId, catalogItemId)).thenReturn(Mono.just(item));

        webClient.get()
                .uri(BASE_PATH + "/{catalogItemId}", distributorId, catalogItemId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CatalogItemDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(catalogItemId));
    }

    @Test
    void PUT_updateCatalogItem_shouldReturn200WithId() {
        UUID distributorId = UUID.randomUUID();
        UUID catalogItemId = UUID.randomUUID();

        when(catalogService.updateCatalogItem(eq(distributorId), eq(catalogItemId), any(UpdateCatalogItemRequest.class)))
                .thenReturn(Mono.just(catalogItemId));

        webClient.put()
                .uri(BASE_PATH + "/{catalogItemId}", distributorId, catalogItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"productName":"Updated","isActive":true}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(catalogItemId));

        verify(catalogService).updateCatalogItem(eq(distributorId), eq(catalogItemId), any());
    }

    @Test
    void DELETE_removeFromCatalog_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID catalogItemId = UUID.randomUUID();

        when(catalogService.removeFromCatalog(distributorId, catalogItemId))
                .thenReturn(Mono.just(catalogItemId));

        webClient.delete()
                .uri(BASE_PATH + "/{catalogItemId}", distributorId, catalogItemId)
                .exchange()
                .expectStatus().isNoContent();

        verify(catalogService).removeFromCatalog(distributorId, catalogItemId);
    }
}
