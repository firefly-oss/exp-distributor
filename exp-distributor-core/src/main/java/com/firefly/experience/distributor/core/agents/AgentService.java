package com.firefly.experience.distributor.core.agents;

import com.firefly.experience.distributor.interfaces.dtos.AgentDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgentRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AgentService {

    Flux<AgentDTO> listAgents(UUID distributorId);

    Mono<AgentDTO> createAgent(UUID distributorId, CreateAgentRequest request);

    Mono<AgentDTO> getAgent(UUID distributorId, UUID agentId);

    Mono<AgentDTO> updateAgent(UUID distributorId, UUID agentId, UpdateAgentRequest request);

    Mono<Void> deleteAgent(UUID distributorId, UUID agentId);
}
