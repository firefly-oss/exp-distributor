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


package io.fireflysoftware.experience.distributor.web.controllers;

import io.fireflysoftware.experience.distributor.core.simulations.SimulationService;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.SimulationResultDTO;
import org.fireflyframework.web.error.config.ErrorHandlingProperties;
import org.fireflyframework.web.error.converter.ExceptionConverterService;
import org.fireflyframework.web.error.service.ErrorResponseNegotiator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = SimulationController.class,
        excludeAutoConfiguration = {
                ReactiveWebSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class SimulationControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private SimulationService simulationService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockitoBean
    private ExceptionConverterService exceptionConverterService;
    @MockitoBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockitoBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/simulations";

    private SimulationResultDTO buildSimulationResultDTO(UUID distributorId, UUID simulationId) {
        return SimulationResultDTO.builder()
                .id(simulationId)
                .distributorId(distributorId)
                .status("COMPLETED")
                .build();
    }

    @Test
    void POST_createSimulation_shouldReturn201WithSimulationId() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();

        when(simulationService.createSimulation(eq(distributorId), any(CreateSimulationRequest.class)))
                .thenReturn(Mono.just(simulationId));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"agentId":"%s","agencyId":"%s"}
                        """.formatted(UUID.randomUUID(), UUID.randomUUID()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(simulationId));
    }

    @Test
    void GET_getSimulation_shouldReturn200WithSimulationResultDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();
        SimulationResultDTO dto = buildSimulationResultDTO(distributorId, simulationId);

        when(simulationService.getSimulation(distributorId, simulationId)).thenReturn(Mono.just(dto));

        webClient.get()
                .uri(BASE_PATH + "/{simulationId}", distributorId, simulationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationResultDTO.class)
                .value(result -> {
                    assertThat(result.getId()).isEqualTo(simulationId);
                    assertThat(result.getDistributorId()).isEqualTo(distributorId);
                    assertThat(result.getStatus()).isEqualTo("COMPLETED");
                });

        verify(simulationService).getSimulation(distributorId, simulationId);
    }
}
