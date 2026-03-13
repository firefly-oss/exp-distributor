package com.firefly.experience.distributor.core.agents;

import com.firefly.domain.distributor.branding.sdk.api.AgentAgencyApi;
import com.firefly.domain.distributor.branding.sdk.model.AssignAgentAgencyCommand;
import com.firefly.experience.distributor.core.mappers.AgentAgencyMapper;
import com.firefly.experience.distributor.interfaces.dtos.AgentAgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.AssignAgentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentAgencyServiceImplTest {

    @Mock
    private AgentAgencyApi agentAgencyApi;

    @Mock
    private AgentAgencyMapper agentAgencyMapper;

    @InjectMocks
    private AgentAgencyServiceImpl service;

    @Test
    void listAssignments_shouldReturnEmptyFluxBecauseSdkHasNoBody() {
        // The SDK listAgentAgencies returns Mono<Void> (no response body).
        // The implementation does .thenMany(Flux.empty()), so the result is always empty.
        UUID distributorId = UUID.randomUUID();

        when(agentAgencyApi.listAgentAgencies(distributorId)).thenReturn(Mono.empty());

        StepVerifier.create(service.listAssignments(distributorId))
                .verifyComplete();

        verify(agentAgencyApi).listAgentAgencies(distributorId);
    }

    @Test
    void assignAgentToAgency_shouldMapRequestAndBuildDtoFromReturnedId() {
        UUID distributorId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID relationshipId = UUID.randomUUID();

        AssignAgentRequest request = AssignAgentRequest.builder()
                .agentId(agentId)
                .agencyId(agencyId)
                .roleId(roleId)
                .isPrimaryAgency(true)
                .build();
        AssignAgentAgencyCommand command = new AssignAgentAgencyCommand();

        when(agentAgencyMapper.toCommand(request)).thenReturn(command);
        when(agentAgencyApi.assignAgentAgency(distributorId, command)).thenReturn(Mono.just(relationshipId));

        StepVerifier.create(service.assignAgentToAgency(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(relationshipId);
                    assertThat(dto.getAgentId()).isEqualTo(agentId);
                    assertThat(dto.getAgencyId()).isEqualTo(agencyId);
                    assertThat(dto.getRoleId()).isEqualTo(roleId);
                    assertThat(dto.getIsPrimaryAgency()).isTrue();
                    assertThat(dto.getIsActive()).isTrue();
                })
                .verifyComplete();

        verify(agentAgencyMapper).toCommand(request);
        verify(agentAgencyApi).assignAgentAgency(distributorId, command);
    }

    @Test
    void unassignAgent_shouldDelegateToApiAndComplete() {
        UUID distributorId = UUID.randomUUID();
        UUID relationshipId = UUID.randomUUID();

        when(agentAgencyApi.removeAgentAgency(distributorId, relationshipId)).thenReturn(Mono.empty());

        StepVerifier.create(service.unassignAgent(distributorId, relationshipId))
                .verifyComplete();

        verify(agentAgencyApi).removeAgentAgency(distributorId, relationshipId);
    }
}
