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


package io.fireflysoftware.experience.distributor.core.agents;

import io.fireflysoftware.domain.distributor.branding.sdk.api.AgentAgencyApi;
import io.fireflysoftware.domain.distributor.branding.sdk.model.AssignAgentAgencyCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AgentAgencyDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AssignAgentRequest;
import io.fireflysoftware.experience.distributor.core.mappers.AgentAgencyMapper;
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
        return agentAgencyApi.listAgentAgencies(distributorId, null)
                .thenMany(Flux.empty());
    }

    @Override
    public Mono<AgentAgencyDTO> assignAgentToAgency(UUID distributorId, AssignAgentRequest request) {
        AssignAgentAgencyCommand command = agentAgencyMapper.toCommand(request);

        return agentAgencyApi.assignAgentAgency(distributorId, command, UUID.randomUUID().toString())
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
        return agentAgencyApi.removeAgentAgency(distributorId, relationshipId, UUID.randomUUID().toString());
    }
}
