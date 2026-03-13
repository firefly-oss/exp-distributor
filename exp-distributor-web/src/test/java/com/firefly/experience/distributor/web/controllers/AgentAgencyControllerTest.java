package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.agents.AgentAgencyService;
import com.firefly.experience.distributor.interfaces.dtos.AgentAgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.AssignAgentRequest;
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

@WebFluxTest(controllers = AgentAgencyController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class AgentAgencyControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private AgentAgencyService agentAgencyService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/agent-agencies";

    private AgentAgencyDTO buildAssignmentDTO(UUID relationshipId, UUID agentId, UUID agencyId) {
        return AgentAgencyDTO.builder()
                .id(relationshipId)
                .agentId(agentId)
                .agencyId(agencyId)
                .isPrimaryAgency(true)
                .isActive(true)
                .build();
    }

    @Test
    void GET_listAssignments_shouldReturn200WithAssignmentList() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();
        UUID relationshipId = UUID.randomUUID();
        AgentAgencyDTO assignment = buildAssignmentDTO(relationshipId, agentId, agencyId);

        when(agentAgencyService.listAssignments(distributorId)).thenReturn(Flux.just(assignment));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AgentAgencyDTO.class)
                .value(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getId()).isEqualTo(relationshipId);
                    assertThat(list.get(0).getAgentId()).isEqualTo(agentId);
                    assertThat(list.get(0).getAgencyId()).isEqualTo(agencyId);
                });

        verify(agentAgencyService).listAssignments(distributorId);
    }

    @Test
    void POST_assignAgentToAgency_shouldReturn201WithAssignmentDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();
        UUID relationshipId = UUID.randomUUID();
        AgentAgencyDTO assignment = buildAssignmentDTO(relationshipId, agentId, agencyId);

        when(agentAgencyService.assignAgentToAgency(eq(distributorId), any(AssignAgentRequest.class)))
                .thenReturn(Mono.just(assignment));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"agentId":"%s","agencyId":"%s","isPrimaryAgency":true}
                        """.formatted(agentId, agencyId))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AgentAgencyDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(relationshipId);
                    assertThat(dto.getAgentId()).isEqualTo(agentId);
                    assertThat(dto.getIsPrimaryAgency()).isTrue();
                });

        verify(agentAgencyService).assignAgentToAgency(eq(distributorId), any(AssignAgentRequest.class));
    }

    @Test
    void DELETE_unassignAgent_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID relationshipId = UUID.randomUUID();

        when(agentAgencyService.unassignAgent(distributorId, relationshipId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{relationshipId}", distributorId, relationshipId)
                .exchange()
                .expectStatus().isNoContent();

        verify(agentAgencyService).unassignAgent(distributorId, relationshipId);
    }
}
