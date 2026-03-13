package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.agents.AgentService;
import com.firefly.experience.distributor.interfaces.dtos.AgentDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgentRequest;
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

@WebFluxTest(controllers = AgentController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class AgentControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private AgentService agentService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/agents";

    private AgentDTO buildAgentDTO(UUID distributorId, UUID agentId) {
        return AgentDTO.builder()
                .id(agentId)
                .distributorId(distributorId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .employeeCode("EMP-001")
                .isActive(true)
                .build();
    }

    @Test
    void GET_listAgents_shouldReturn200WithAgentList() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        AgentDTO agent = buildAgentDTO(distributorId, agentId);

        when(agentService.listAgents(distributorId)).thenReturn(Flux.just(agent));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AgentDTO.class)
                .value(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getId()).isEqualTo(agentId);
                    assertThat(list.get(0).getFirstName()).isEqualTo("John");
                });

        verify(agentService).listAgents(distributorId);
    }

    @Test
    void POST_createAgent_shouldReturn201WithCreatedAgent() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        AgentDTO agent = buildAgentDTO(distributorId, agentId);

        when(agentService.createAgent(eq(distributorId), any(CreateAgentRequest.class)))
                .thenReturn(Mono.just(agent));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"firstName":"John","lastName":"Doe","email":"john.doe@example.com","employeeCode":"EMP-001","isActive":true}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AgentDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(agentId);
                    assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
                });
    }

    @Test
    void GET_getAgent_shouldReturn200WithAgentDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        AgentDTO agent = buildAgentDTO(distributorId, agentId);

        when(agentService.getAgent(distributorId, agentId)).thenReturn(Mono.just(agent));

        webClient.get()
                .uri(BASE_PATH + "/{agentId}", distributorId, agentId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AgentDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(agentId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getLastName()).isEqualTo("Doe");
                    assertThat(dto.getIsActive()).isTrue();
                });

        verify(agentService).getAgent(distributorId, agentId);
    }

    @Test
    void PUT_updateAgent_shouldReturn200WithUpdatedAgent() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        AgentDTO agent = buildAgentDTO(distributorId, agentId);

        when(agentService.updateAgent(eq(distributorId), eq(agentId), any(UpdateAgentRequest.class)))
                .thenReturn(Mono.just(agent));

        webClient.put()
                .uri(BASE_PATH + "/{agentId}", distributorId, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"firstName":"Jane","lastName":"Doe","isActive":false}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AgentDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(agentId));

        verify(agentService).updateAgent(eq(distributorId), eq(agentId), any(UpdateAgentRequest.class));
    }

    @Test
    void DELETE_deleteAgent_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();

        when(agentService.deleteAgent(distributorId, agentId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{agentId}", distributorId, agentId)
                .exchange()
                .expectStatus().isNoContent();

        verify(agentService).deleteAgent(distributorId, agentId);
    }
}
