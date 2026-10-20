package io.fireflysoftware.experience.distributor.core.network;

import io.fireflysoftware.domain.distributor.branding.sdk.model.PaginationResponse;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.TerritoryDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TerritoryService {

    Mono<UUID> createTerritory(UUID distributorId, CreateTerritoryRequest request);

    Mono<TerritoryDTO> getTerritory(UUID distributorId, UUID territoryId);

    Mono<PaginationResponse> listTerritories(UUID distributorId);

    Mono<UUID> updateTerritory(UUID distributorId, UUID territoryId, UpdateTerritoryRequest request);

    Mono<Void> deleteTerritory(UUID distributorId, UUID territoryId);
}
