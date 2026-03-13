package com.firefly.experience.distributor.core.agents;

import com.firefly.domain.distributor.branding.sdk.api.AgentAgencyApi;
import com.firefly.domain.distributor.branding.sdk.model.AssignAgentAgencyCommand;
import com.firefly.experience.distributor.interfaces.dtos.AgentAgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.AssignAgentRequest;
import com.firefly.experience.distributor.core.mappers.AgentAgencyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentAgencyServiceImpl implements AgentAgencyService {

    private final AgentAgencyApi agentAgencyApi;
    private final AgentAgencyMapper agentAgencyMapper;

    @Override
    public Flux<AgentAgencyDTO> listAssignments(UUID distributorId) {
        // The SDK listAgentAgencies returns Mono<Void> (no body), so return empty flux
        return agentAgencyApi.listAgentAgencies(distributorId)
                .thenMany(Flux.empty());
    }

    @Override
    public Mono<AgentAgencyDTO> assignAgentToAgency(UUID distributorId, AssignAgentRequest request) {
        AssignAgentAgencyCommand command = agentAgencyMapper.toCommand(request);

        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on assignAgentAgency; idempotency cannot be set at call-site.
        return agentAgencyApi.assignAgentAgency(distributorId, command)
                .map(id -> AgentAgencyDTO.builder()
                        .id(id)
                        .agentId(request.getAgentId())
                        .agencyId(request.getAgencyId())
                        .roleId(request.getRoleId())
                        .isPrimaryAgency(request.getIsPrimaryAgency())
                        .isActive(true)
                        .build());
    }

    @Override
    public Mono<Void> unassignAgent(UUID distributorId, UUID relationshipId) {
        return agentAgencyApi.removeAgentAgency(distributorId, relationshipId);
    }
}
