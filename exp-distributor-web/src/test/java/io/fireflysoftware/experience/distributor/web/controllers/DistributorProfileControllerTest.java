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

import io.fireflysoftware.experience.distributor.core.profile.DistributorProfileService;
import io.fireflysoftware.experience.distributor.interfaces.dtos.BrandingDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.DistributorDetailDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateDistributorRequest;
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

@WebFluxTest(controllers = DistributorProfileController.class,
        excludeAutoConfiguration = {
                ReactiveWebSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class DistributorProfileControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private DistributorProfileService distributorProfileService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockitoBean
    private ExceptionConverterService exceptionConverterService;
    @MockitoBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockitoBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private DistributorDetailDTO buildDetailDTO(UUID distributorId) {
        return DistributorDetailDTO.builder()
                .id(distributorId)
                .name("Acme Corp")
                .displayName("Acme")
                .email("info@acme.com")
                .status("ACTIVE")
                .hasActiveSignedTerms(true)
                .activeBranding(BrandingDTO.builder()
                        .id(UUID.randomUUID())
                        .logoUrl("https://example.com/logo.png")
                        .isDefault(true)
                        .build())
                .build();
    }

    @Test
    void POST_registerDistributor_shouldReturn201WithDetailDTO() {
        UUID distributorId = UUID.randomUUID();
        DistributorDetailDTO response = buildDetailDTO(distributorId);

        when(distributorProfileService.registerDistributor(any(RegisterDistributorRequest.class)))
                .thenReturn(Mono.just(response));

        webClient.post()
                .uri("/api/v1/experience/distributors")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Acme Corp","displayName":"Acme","email":"info@acme.com"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DistributorDetailDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(distributorId);
                    assertThat(dto.getStatus()).isEqualTo("ACTIVE");
                });
    }

    @Test
    void GET_getDistributorDetail_shouldReturn200WithDetailDTO() {
        UUID distributorId = UUID.randomUUID();
        DistributorDetailDTO response = buildDetailDTO(distributorId);

        when(distributorProfileService.getDistributorDetail(distributorId))
                .thenReturn(Mono.just(response));

        webClient.get()
                .uri("/api/v1/experience/distributors/{id}", distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DistributorDetailDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(distributorId));
    }

    @Test
    void PUT_updateDistributor_shouldReturn200WithDetailDTO() {
        UUID distributorId = UUID.randomUUID();
        DistributorDetailDTO response = buildDetailDTO(distributorId);

        when(distributorProfileService.updateDistributor(eq(distributorId), any(UpdateDistributorRequest.class)))
                .thenReturn(Mono.just(response));

        webClient.put()
                .uri("/api/v1/experience/distributors/{id}", distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Acme Corp","displayName":"Acme","email":"info@acme.com"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DistributorDetailDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(distributorId));

        verify(distributorProfileService).updateDistributor(eq(distributorId), any());
    }

    @Test
    void DELETE_deleteDistributor_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        when(distributorProfileService.deleteDistributor(distributorId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri("/api/v1/experience/distributors/{id}", distributorId)
                .exchange()
                .expectStatus().isNoContent();

        verify(distributorProfileService).deleteDistributor(distributorId);
    }
}
