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


package io.fireflysoftware.experience.distributor.core.simulations;

import io.fireflysoftware.domain.distributor.catalog.sdk.api.SimulationsApi;
import io.fireflysoftware.domain.distributor.catalog.sdk.model.CreateSimulationCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.SimulationResultDTO;
import io.fireflysoftware.experience.distributor.core.mappers.SimulationMapper;
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
        return simulationsApi.createSimulation(distributorId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<SimulationResultDTO> getSimulation(UUID distributorId, UUID simulationId) {
        log.info("Getting simulation {} for distributor: {}", simulationId, distributorId);
        return simulationsApi.getSimulation(distributorId, simulationId, null)
                .map(simulationMapper::toDto);
    }
}
