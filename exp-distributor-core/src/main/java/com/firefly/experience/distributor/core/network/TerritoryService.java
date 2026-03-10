package com.firefly.experience.distributor.core.network;

import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import com.firefly.experience.distributor.interfaces.dtos.TerritoryDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TerritoryService {

    Mono<UUID> createTerritory(UUID distributorId, CreateTerritoryRequest request);

    Mono<TerritoryDTO> getTerritory(UUID distributorId, UUID territoryId);

    Mono<PaginationResponse> listTerritories(UUID distributorId);

    Mono<UUID> updateTerritory(UUID distributorId, UUID territoryId, UpdateTerritoryRequest request);

    Mono<Void> deleteTerritory(UUID distributorId, UUID territoryId);
}
