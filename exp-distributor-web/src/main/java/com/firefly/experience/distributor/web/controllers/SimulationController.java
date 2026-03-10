package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.simulations.SimulationService;
import com.firefly.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import com.firefly.experience.distributor.interfaces.dtos.SimulationResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/simulations")
@RequiredArgsConstructor
@Tag(name = "Simulations", description = "Distributor simulation management")
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping
    @Operation(summary = "Create simulation", description = "Create a new simulation for a distributor")
    public Mono<ResponseEntity<UUID>> createSimulation(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateSimulationRequest request) {
        return simulationService.createSimulation(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @GetMapping("/{simulationId}")
    @Operation(summary = "Get simulation", description = "Get a simulation result by ID")
    public Mono<ResponseEntity<SimulationResultDTO>> getSimulation(
            @PathVariable UUID distributorId,
            @PathVariable UUID simulationId) {
        return simulationService.getSimulation(distributorId, simulationId)
                .map(ResponseEntity::ok);
    }
}
