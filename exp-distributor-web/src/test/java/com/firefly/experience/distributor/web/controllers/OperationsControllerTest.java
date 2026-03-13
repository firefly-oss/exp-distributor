package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.operations.OperationsService;
import com.firefly.experience.distributor.interfaces.dtos.CreateOperationRequest;
import com.firefly.experience.distributor.interfaces.dtos.OperationDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateOperationRequest;
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

@WebFluxTest(controllers = OperationsController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class OperationsControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private OperationsService operationsService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/operations";

    private OperationDTO buildOperationDTO(UUID distributorId, UUID operationId) {
        return OperationDTO.builder()
                .id(operationId)
                .distributorId(distributorId)
                .isActive(true)
                .build();
    }

    @Test
    void GET_listOperations_shouldReturn200WithListOfOperations() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        OperationDTO dto = buildOperationDTO(distributorId, operationId);

        when(operationsService.listOperations(distributorId)).thenReturn(Flux.just(dto));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OperationDTO.class)
                .value(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getId()).isEqualTo(operationId);
                });

        verify(operationsService).listOperations(distributorId);
    }

    @Test
    void POST_createOperation_shouldReturn201WithOperationDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        OperationDTO dto = buildOperationDTO(distributorId, operationId);

        when(operationsService.createOperation(eq(distributorId), any(CreateOperationRequest.class)))
                .thenReturn(Mono.just(dto));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"countryId":"%s","isActive":true}
                        """.formatted(UUID.randomUUID()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OperationDTO.class)
                .value(result -> {
                    assertThat(result.getId()).isEqualTo(operationId);
                    assertThat(result.getDistributorId()).isEqualTo(distributorId);
                });
    }

    @Test
    void PUT_updateOperation_shouldReturn200WithUpdatedOperationDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        OperationDTO dto = buildOperationDTO(distributorId, operationId);

        when(operationsService.updateOperation(eq(distributorId), eq(operationId), any(UpdateOperationRequest.class)))
                .thenReturn(Mono.just(dto));

        webClient.put()
                .uri(BASE_PATH + "/{operationId}", distributorId, operationId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"countryId":"%s"}
                        """.formatted(UUID.randomUUID()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(OperationDTO.class)
                .value(result -> assertThat(result.getId()).isEqualTo(operationId));

        verify(operationsService).updateOperation(eq(distributorId), eq(operationId), any(UpdateOperationRequest.class));
    }

    @Test
    void DELETE_deleteOperation_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        when(operationsService.deleteOperation(distributorId, operationId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{operationId}", distributorId, operationId)
                .exchange()
                .expectStatus().isNoContent();

        verify(operationsService).deleteOperation(distributorId, operationId);
    }

    @Test
    void PATCH_activateOperation_shouldReturn200WithActivatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        OperationDTO dto = buildOperationDTO(distributorId, operationId);

        when(operationsService.activateOperation(distributorId, operationId)).thenReturn(Mono.just(dto));

        webClient.patch()
                .uri(BASE_PATH + "/{operationId}/activate", distributorId, operationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OperationDTO.class)
                .value(result -> {
                    assertThat(result.getId()).isEqualTo(operationId);
                    assertThat(result.getIsActive()).isTrue();
                });

        verify(operationsService).activateOperation(distributorId, operationId);
    }

    @Test
    void PATCH_deactivateOperation_shouldReturn200WithDeactivatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        OperationDTO dto = buildOperationDTO(distributorId, operationId);

        when(operationsService.deactivateOperation(distributorId, operationId)).thenReturn(Mono.just(dto));

        webClient.patch()
                .uri(BASE_PATH + "/{operationId}/deactivate", distributorId, operationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OperationDTO.class)
                .value(result -> assertThat(result.getId()).isEqualTo(operationId));

        verify(operationsService).deactivateOperation(distributorId, operationId);
    }

    @Test
    void GET_canOperate_shouldReturn200WithBoolean() {
        UUID distributorId = UUID.randomUUID();

        when(operationsService.canOperate(distributorId)).thenReturn(Mono.just(true));

        webClient.get()
                .uri(BASE_PATH + "/can-operate", distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .value(result -> assertThat(result).isTrue());

        verify(operationsService).canOperate(distributorId);
    }
}
