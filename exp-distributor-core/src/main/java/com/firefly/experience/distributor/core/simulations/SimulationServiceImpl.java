package com.firefly.experience.distributor.core.simulations;

import com.firefly.domain.distributor.catalog.sdk.api.SimulationsApi;
import com.firefly.domain.distributor.catalog.sdk.model.CreateSimulationCommand;
import com.firefly.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import com.firefly.experience.distributor.interfaces.dtos.SimulationResultDTO;
import com.firefly.experience.distributor.core.mappers.SimulationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final SimulationsApi simulationsApi;
    private final SimulationMapper simulationMapper;

    @Override
    public Mono<UUID> createSimulation(UUID distributorId, CreateSimulationRequest request) {
        log.info("Creating simulation for distributor: {}", distributorId);
        CreateSimulationCommand command = simulationMapper.toCommand(request);
        command.setDistributorId(distributorId);
        // ARCH-EXCEPTION: domain-distributor-catalog-sdk generated client does not expose an
        // xIdempotencyKey parameter on createSimulation; idempotency cannot be set at call-site.
        return simulationsApi.createSimulation(distributorId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<SimulationResultDTO> getSimulation(UUID distributorId, UUID simulationId) {
        log.info("Getting simulation {} for distributor: {}", simulationId, distributorId);
        return simulationsApi.getSimulation(distributorId, simulationId, UUID.randomUUID().toString())
                .map(simulationMapper::toDto);
    }
}
