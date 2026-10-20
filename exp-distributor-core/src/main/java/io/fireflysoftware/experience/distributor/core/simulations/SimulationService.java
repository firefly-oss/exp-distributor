package io.fireflysoftware.experience.distributor.core.simulations;

import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.SimulationResultDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SimulationService {

    Mono<UUID> createSimulation(UUID distributorId, CreateSimulationRequest request);

    Mono<SimulationResultDTO> getSimulation(UUID distributorId, UUID simulationId);
}
