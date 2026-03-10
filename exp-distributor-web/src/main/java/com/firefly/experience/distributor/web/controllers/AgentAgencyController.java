package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.agents.AgentAgencyService;
import com.firefly.experience.distributor.interfaces.dtos.AgentAgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.AssignAgentRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/agent-agencies")
@RequiredArgsConstructor
@Tag(name = "Agent-Agency Assignments", description = "Manage agent-to-agency assignments")
public class AgentAgencyController {

    private final AgentAgencyService agentAgencyService;

    @GetMapping
    @Operation(summary = "List assignments", description = "List all agent-agency assignments for a distributor")
    public Mono<ResponseEntity<List<AgentAgencyDTO>>> listAssignments(
            @PathVariable UUID distributorId) {
        return agentAgencyService.listAssignments(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Assign agent to agency", description = "Create an agent-agency assignment for a distributor")
    public Mono<ResponseEntity<AgentAgencyDTO>> assignAgentToAgency(
            @PathVariable UUID distributorId,
            @Valid @RequestBody AssignAgentRequest request) {
        return agentAgencyService.assignAgentToAgency(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @DeleteMapping("/{relationshipId}")
    @Operation(summary = "Unassign agent", description = "Remove an agent-agency assignment from a distributor")
    public Mono<ResponseEntity<Void>> unassignAgent(
            @PathVariable UUID distributorId,
            @PathVariable UUID relationshipId) {
        return agentAgencyService.unassignAgent(distributorId, relationshipId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
