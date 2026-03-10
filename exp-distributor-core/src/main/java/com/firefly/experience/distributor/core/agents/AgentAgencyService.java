package com.firefly.experience.distributor.core.agents;

import com.firefly.experience.distributor.interfaces.dtos.AgentAgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.AssignAgentRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AgentAgencyService {

    Flux<AgentAgencyDTO> listAssignments(UUID distributorId);

    Mono<AgentAgencyDTO> assignAgentToAgency(UUID distributorId, AssignAgentRequest request);

    Mono<Void> unassignAgent(UUID distributorId, UUID relationshipId);
}
