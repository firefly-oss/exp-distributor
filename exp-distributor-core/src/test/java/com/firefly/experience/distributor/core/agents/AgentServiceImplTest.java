package com.firefly.experience.distributor.core.agents;

import com.firefly.domain.distributor.branding.sdk.api.AgentApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateAgentCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorAgentDTO;
import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.domain.distributor.branding.sdk.model.UpdateAgentCommand;
import com.firefly.experience.distributor.core.mappers.AgentMapper;
import com.firefly.experience.distributor.interfaces.dtos.AgentDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentServiceImplTest {

    @Mock
    private AgentApi agentApi;

    @Mock
    private AgentMapper agentMapper;

    @InjectMocks
    private AgentServiceImpl service;

    private DistributorAgentDTO buildSdkAgent(UUID distributorId, UUID agentId) {
        DistributorAgentDTO dto = new DistributorAgentDTO();
        dto.setId(agentId);
        dto.setDistributorId(distributorId);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setEmployeeCode("EMP-001");
        return dto;
    }

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
    void listAgents_shouldFlattenPaginationContentAndMapEachItem() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        DistributorAgentDTO sdkAgent = buildSdkAgent(distributorId, agentId);
        AgentDTO expected = buildAgentDTO(distributorId, agentId);

        PaginationResponse page = new PaginationResponse();
        page.setContent(List.of(sdkAgent));

        when(agentApi.listAgents(eq(distributorId), any())).thenReturn(Mono.just(page));
        when(agentMapper.toDto(sdkAgent)).thenReturn(expected);

        StepVerifier.create(service.listAgents(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(agentId);
                    assertThat(dto.getFirstName()).isEqualTo("John");
                })
                .verifyComplete();

        verify(agentApi).listAgents(eq(distributorId), any());
        verify(agentMapper).toDto(sdkAgent);
    }

    @Test
    void listAgents_shouldReturnEmptyFluxWhenPaginationContentIsEmpty() {
        UUID distributorId = UUID.randomUUID();
        PaginationResponse page = new PaginationResponse();
        page.setContent(List.of());

        when(agentApi.listAgents(eq(distributorId), any())).thenReturn(Mono.just(page));

        StepVerifier.create(service.listAgents(distributorId))
                .verifyComplete();

        verify(agentApi).listAgents(eq(distributorId), any());
    }

    @Test
    void createAgent_shouldMapRequestToCommandThenFetchCreatedAgent() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        CreateAgentRequest request = CreateAgentRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .employeeCode("EMP-001")
                .isActive(true)
                .build();
        CreateAgentCommand command = new CreateAgentCommand();
        DistributorAgentDTO sdkAgent = buildSdkAgent(distributorId, agentId);
        AgentDTO expected = buildAgentDTO(distributorId, agentId);

        when(agentMapper.toCreateCommand(request)).thenReturn(command);
        when(agentApi.createAgent(eq(distributorId), eq(command), any())).thenReturn(Mono.just(agentId));
        when(agentApi.getAgent(eq(distributorId), eq(agentId), any())).thenReturn(Mono.just(sdkAgent));
        when(agentMapper.toDto(sdkAgent)).thenReturn(expected);

        StepVerifier.create(service.createAgent(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(agentId);
                    assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
                })
                .verifyComplete();

        verify(agentMapper).toCreateCommand(request);
        verify(agentApi).createAgent(eq(distributorId), eq(command), any());
        verify(agentApi).getAgent(eq(distributorId), eq(agentId), any());
    }

    @Test
    void getAgent_shouldDelegateToApiAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        DistributorAgentDTO sdkAgent = buildSdkAgent(distributorId, agentId);
        AgentDTO expected = buildAgentDTO(distributorId, agentId);

        when(agentApi.getAgent(eq(distributorId), eq(agentId), any())).thenReturn(Mono.just(sdkAgent));
        when(agentMapper.toDto(sdkAgent)).thenReturn(expected);

        StepVerifier.create(service.getAgent(distributorId, agentId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(agentId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getLastName()).isEqualTo("Doe");
                })
                .verifyComplete();

        verify(agentApi).getAgent(eq(distributorId), eq(agentId), any());
        verify(agentMapper).toDto(sdkAgent);
    }

    @Test
    void updateAgent_shouldMapRequestToCommandThenFetchUpdatedAgent() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UpdateAgentRequest request = UpdateAgentRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .isActive(false)
                .build();
        UpdateAgentCommand command = new UpdateAgentCommand();
        DistributorAgentDTO sdkAgent = buildSdkAgent(distributorId, agentId);
        AgentDTO expected = buildAgentDTO(distributorId, agentId);

        when(agentMapper.toUpdateCommand(request)).thenReturn(command);
        when(agentApi.updateAgent(eq(distributorId), eq(agentId), eq(command), any())).thenReturn(Mono.just(agentId));
        when(agentApi.getAgent(eq(distributorId), eq(agentId), any())).thenReturn(Mono.just(sdkAgent));
        when(agentMapper.toDto(sdkAgent)).thenReturn(expected);

        StepVerifier.create(service.updateAgent(distributorId, agentId, request))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(agentId))
                .verifyComplete();

        verify(agentMapper).toUpdateCommand(request);
        verify(agentApi).updateAgent(eq(distributorId), eq(agentId), eq(command), any());
        verify(agentApi).getAgent(eq(distributorId), eq(agentId), any());
    }

    @Test
    void deleteAgent_shouldDelegateToApiAndComplete() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();

        when(agentApi.deleteAgent(eq(distributorId), eq(agentId), any())).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteAgent(distributorId, agentId))
                .verifyComplete();

        verify(agentApi).deleteAgent(eq(distributorId), eq(agentId), any());
    }
}
