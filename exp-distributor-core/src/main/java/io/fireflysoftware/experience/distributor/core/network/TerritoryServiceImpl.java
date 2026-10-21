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


package io.fireflysoftware.experience.distributor.core.network;

import io.fireflysoftware.domain.distributor.branding.sdk.api.TerritoryApi;
import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateTerritoryCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateTerritoryCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.TerritoryDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
import io.fireflysoftware.experience.distributor.core.mappers.TerritoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TerritoryServiceImpl implements TerritoryService {

    private final TerritoryApi territoryApi;
    private final TerritoryMapper territoryMapper;

    @Override
    public Mono<UUID> createTerritory(UUID distributorId, CreateTerritoryRequest request) {
        log.info("Creating territory for distributor: {}", distributorId);

        CreateTerritoryCommand command = territoryMapper.toCreateCommand(request);

        return territoryApi.createTerritory(distributorId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<TerritoryDTO> getTerritory(UUID distributorId, UUID territoryId) {
        log.info("Getting territory {} for distributor: {}", territoryId, distributorId);
        return territoryApi.getTerritory(distributorId, territoryId, null)
                .map(territoryMapper::toDto);
    }

    @Override
    public Mono<PaginationResponse> listTerritories(UUID distributorId) {
        log.info("Listing territories for distributor: {}", distributorId);
        return territoryApi.listTerritories(distributorId, null);
    }

    @Override
    public Mono<UUID> updateTerritory(UUID distributorId, UUID territoryId, UpdateTerritoryRequest request) {
        log.info("Updating territory {} for distributor: {}", territoryId, distributorId);

        UpdateTerritoryCommand command = territoryMapper.toUpdateCommand(request);

        return territoryApi.updateTerritory(distributorId, territoryId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<Void> deleteTerritory(UUID distributorId, UUID territoryId) {
        log.info("Deleting territory {} for distributor: {}", territoryId, distributorId);
        return territoryApi.deleteTerritory(distributorId, territoryId, UUID.randomUUID().toString());
    }
}
