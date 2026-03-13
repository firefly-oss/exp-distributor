package com.firefly.experience.distributor.web.controllers;

import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.experience.distributor.core.network.TerritoryService;
import com.firefly.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import com.firefly.experience.distributor.interfaces.dtos.TerritoryDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
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
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = TerritoryController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class TerritoryControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private TerritoryService territoryService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/territories";

    private TerritoryDTO buildTerritoryDTO(UUID distributorId, UUID territoryId) {
        return TerritoryDTO.builder()
                .id(territoryId)
                .distributorId(distributorId)
                .authorizationLevel("COUNTRY")
                .isActive(true)
                .build();
    }

    @Test
    void GET_listTerritories_shouldReturn200WithPaginationResponse() {
        UUID distributorId = UUID.randomUUID();
        PaginationResponse response = new PaginationResponse();

        when(territoryService.listTerritories(distributorId)).thenReturn(Mono.just(response));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginationResponse.class);

        verify(territoryService).listTerritories(distributorId);
    }

    @Test
    void POST_createTerritory_shouldReturn201WithTerritoryId() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();

        when(territoryService.createTerritory(eq(distributorId), any(CreateTerritoryRequest.class)))
                .thenReturn(Mono.just(territoryId));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"countryId":"%s","authorizationLevel":"COUNTRY","isActive":true}
                        """.formatted(UUID.randomUUID()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(territoryId));
    }

    @Test
    void GET_getTerritory_shouldReturn200WithTerritoryDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();
        TerritoryDTO territory = buildTerritoryDTO(distributorId, territoryId);

        when(territoryService.getTerritory(distributorId, territoryId)).thenReturn(Mono.just(territory));

        webClient.get()
                .uri(BASE_PATH + "/{territoryId}", distributorId, territoryId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TerritoryDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(territoryId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getAuthorizationLevel()).isEqualTo("COUNTRY");
                });
    }

    @Test
    void PUT_updateTerritory_shouldReturn200WithTerritoryId() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();

        when(territoryService.updateTerritory(eq(distributorId), eq(territoryId), any(UpdateTerritoryRequest.class)))
                .thenReturn(Mono.just(territoryId));

        webClient.put()
                .uri(BASE_PATH + "/{territoryId}", distributorId, territoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"authorizationLevel":"REGION","isActive":false}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(territoryId));

        verify(territoryService).updateTerritory(eq(distributorId), eq(territoryId), any(UpdateTerritoryRequest.class));
    }

    @Test
    void DELETE_deleteTerritory_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();

        when(territoryService.deleteTerritory(distributorId, territoryId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{territoryId}", distributorId, territoryId)
                .exchange()
                .expectStatus().isNoContent();

        verify(territoryService).deleteTerritory(distributorId, territoryId);
    }
}
