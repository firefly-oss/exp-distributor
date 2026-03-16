package com.firefly.experience.distributor.core.agents;

import com.firefly.domain.distributor.branding.sdk.api.AgentApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateAgentCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorAgentDTO;
import com.firefly.domain.distributor.branding.sdk.model.UpdateAgentCommand;
import com.firefly.experience.distributor.interfaces.dtos.AgentDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgentRequest;
import com.firefly.experience.distributor.core.mappers.AgentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentApi agentApi;
    private final AgentMapper agentMapper;

    @Override
    public Flux<AgentDTO> listAgents(UUID distributorId) {
        return agentApi.listAgents(distributorId, null)
                .flatMapMany(paginationResponse -> Flux.fromIterable(paginationResponse.getContent()))
                .map(item -> {
                    DistributorAgentDTO sdk = mapObjectToAgentSdk(item);
                    return agentMapper.toDto(sdk);
                });
    }

    @Override
    public Mono<AgentDTO> createAgent(UUID distributorId, CreateAgentRequest request) {
        CreateAgentCommand command = agentMapper.toCreateCommand(request);

        return agentApi.createAgent(distributorId, command, UUID.randomUUID().toString())
                .flatMap(id -> agentApi.getAgent(distributorId, id, null))
                .map(agentMapper::toDto);
    }

    @Override
    public Mono<AgentDTO> getAgent(UUID distributorId, UUID agentId) {
        return agentApi.getAgent(distributorId, agentId, null)
                .map(agentMapper::toDto);
    }

    @Override
    public Mono<AgentDTO> updateAgent(UUID distributorId, UUID agentId, UpdateAgentRequest request) {
        UpdateAgentCommand command = agentMapper.toUpdateCommand(request);

        return agentApi.updateAgent(distributorId, agentId, command, UUID.randomUUID().toString())
                .flatMap(id -> agentApi.getAgent(distributorId, id, null))
                .map(agentMapper::toDto);
    }

    @Override
    public Mono<Void> deleteAgent(UUID distributorId, UUID agentId) {
        return agentApi.deleteAgent(distributorId, agentId, UUID.randomUUID().toString());
    }

    @SuppressWarnings("unchecked")
    private DistributorAgentDTO mapObjectToAgentSdk(Object item) {
        if (item instanceof DistributorAgentDTO) {
            return (DistributorAgentDTO) item;
        }
        if (item instanceof java.util.Map) {
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) item;
            DistributorAgentDTO dto = new DistributorAgentDTO();
            dto.setId(mapUuid(map.get("id")));
            dto.setDistributorId(mapUuid(map.get("distributorId")));
            dto.setFirstName(map.get("firstName") != null ? map.get("firstName").toString() : null);
            dto.setLastName(map.get("lastName") != null ? map.get("lastName").toString() : null);
            dto.setEmail(map.get("email") != null ? map.get("email").toString() : null);
            dto.setEmployeeCode(map.get("employeeCode") != null ? map.get("employeeCode").toString() : null);
            return dto;
        }
        throw new IllegalArgumentException("Cannot map object to DistributorAgentDTO");
    }

    private UUID mapUuid(Object value) {
        if (value == null) return null;
        if (value instanceof UUID) return (UUID) value;
        return UUID.fromString(value.toString());
    }
}
