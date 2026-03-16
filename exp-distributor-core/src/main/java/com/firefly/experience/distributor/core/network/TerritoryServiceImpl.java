package com.firefly.experience.distributor.core.network;

import com.firefly.domain.distributor.branding.sdk.api.TerritoryApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateTerritoryCommand;
import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.domain.distributor.branding.sdk.model.UpdateTerritoryCommand;
import com.firefly.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import com.firefly.experience.distributor.interfaces.dtos.TerritoryDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
import com.firefly.experience.distributor.core.mappers.TerritoryMapper;
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

        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on createTerritory; idempotency cannot be set at call-site.
        return territoryApi.createTerritory(distributorId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<TerritoryDTO> getTerritory(UUID distributorId, UUID territoryId) {
        log.info("Getting territory {} for distributor: {}", territoryId, distributorId);
        return territoryApi.getTerritory(distributorId, territoryId, UUID.randomUUID().toString())
                .map(territoryMapper::toDto);
    }

    @Override
    public Mono<PaginationResponse> listTerritories(UUID distributorId) {
        log.info("Listing territories for distributor: {}", distributorId);
        return territoryApi.listTerritories(distributorId, UUID.randomUUID().toString());
    }

    @Override
    public Mono<UUID> updateTerritory(UUID distributorId, UUID territoryId, UpdateTerritoryRequest request) {
        log.info("Updating territory {} for distributor: {}", territoryId, distributorId);

        UpdateTerritoryCommand command = territoryMapper.toUpdateCommand(request);

        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on updateTerritory; idempotency cannot be set at call-site.
        return territoryApi.updateTerritory(distributorId, territoryId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<Void> deleteTerritory(UUID distributorId, UUID territoryId) {
        log.info("Deleting territory {} for distributor: {}", territoryId, distributorId);
        return territoryApi.deleteTerritory(distributorId, territoryId, UUID.randomUUID().toString());
    }
}
