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


package io.fireflysoftware.experience.distributor.web.controllers;

import io.fireflysoftware.experience.distributor.core.agents.AgentService;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AgentDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateAgentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateAgentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/agents")
@RequiredArgsConstructor
@Tag(name = "Agents", description = "Manage distributor agents")
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    @Operation(summary = "List agents", description = "List all agents for a distributor")
    public Mono<ResponseEntity<List<AgentDTO>>> listAgents(
            @PathVariable UUID distributorId) {
        return agentService.listAgents(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create agent", description = "Create a new agent for a distributor")
    public Mono<ResponseEntity<AgentDTO>> createAgent(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateAgentRequest request) {
        return agentService.createAgent(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @GetMapping("/{agentId}")
    @Operation(summary = "Get agent", description = "Retrieve a single agent by its identifier")
    public Mono<ResponseEntity<AgentDTO>> getAgent(
            @PathVariable UUID distributorId,
            @PathVariable UUID agentId) {
        return agentService.getAgent(distributorId, agentId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{agentId}")
    @Operation(summary = "Update agent", description = "Update an existing agent")
    public Mono<ResponseEntity<AgentDTO>> updateAgent(
            @PathVariable UUID distributorId,
            @PathVariable UUID agentId,
            @Valid @RequestBody UpdateAgentRequest request) {
        return agentService.updateAgent(distributorId, agentId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{agentId}")
    @Operation(summary = "Delete agent", description = "Delete an agent from a distributor")
    public Mono<ResponseEntity<Void>> deleteAgent(
            @PathVariable UUID distributorId,
            @PathVariable UUID agentId) {
        return agentService.deleteAgent(distributorId, agentId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
