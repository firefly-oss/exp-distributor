package com.firefly.experience.distributor.web.controllers;

import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.experience.distributor.core.network.AgencyService;
import com.firefly.experience.distributor.interfaces.dtos.AgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
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

@WebFluxTest(controllers = AgencyController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class AgencyControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private AgencyService agencyService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/agencies";

    private AgencyDTO buildAgencyDTO(UUID distributorId, UUID agencyId) {
        return AgencyDTO.builder()
                .id(agencyId)
                .distributorId(distributorId)
                .name("Main Office")
                .code("MO-001")
                .city("Madrid")
                .isActive(true)
                .isHeadquarters(true)
                .build();
    }

    @Test
    void GET_listAgencies_shouldReturn200WithPaginationResponse() {
        UUID distributorId = UUID.randomUUID();
        PaginationResponse response = new PaginationResponse();

        when(agencyService.listAgencies(distributorId)).thenReturn(Mono.just(response));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginationResponse.class);

        verify(agencyService).listAgencies(distributorId);
    }

    @Test
    void POST_createAgency_shouldReturn201WithAgencyId() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();

        when(agencyService.createAgency(eq(distributorId), any(CreateAgencyRequest.class)))
                .thenReturn(Mono.just(agencyId));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Main Office","code":"MO-001","city":"Madrid","isActive":true}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(agencyId));
    }

    @Test
    void GET_getAgency_shouldReturn200WithAgencyDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();
        AgencyDTO agency = buildAgencyDTO(distributorId, agencyId);

        when(agencyService.getAgency(distributorId, agencyId)).thenReturn(Mono.just(agency));

        webClient.get()
                .uri(BASE_PATH + "/{agencyId}", distributorId, agencyId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AgencyDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(agencyId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getName()).isEqualTo("Main Office");
                    assertThat(dto.getIsHeadquarters()).isTrue();
                });
    }

    @Test
    void PUT_updateAgency_shouldReturn200WithAgencyId() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();

        when(agencyService.updateAgency(eq(distributorId), eq(agencyId), any(UpdateAgencyRequest.class)))
                .thenReturn(Mono.just(agencyId));

        webClient.put()
                .uri(BASE_PATH + "/{agencyId}", distributorId, agencyId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Updated Office","city":"Barcelona","isActive":false}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(agencyId));

        verify(agencyService).updateAgency(eq(distributorId), eq(agencyId), any(UpdateAgencyRequest.class));
    }

    @Test
    void DELETE_deleteAgency_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();

        when(agencyService.deleteAgency(distributorId, agencyId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{agencyId}", distributorId, agencyId)
                .exchange()
                .expectStatus().isNoContent();

        verify(agencyService).deleteAgency(distributorId, agencyId);
    }
}
