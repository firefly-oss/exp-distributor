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

import io.fireflysoftware.domain.distributor.branding.sdk.model.PaginationResponse;
import io.fireflysoftware.experience.distributor.core.network.TerritoryService;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.TerritoryDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/territories")
@RequiredArgsConstructor
@Tag(name = "Territories", description = "Manage distributor authorized territories")
public class TerritoryController {

    private final TerritoryService territoryService;

    @GetMapping
    @Operation(summary = "List territories", description = "List all authorized territories for a distributor")
    public Mono<ResponseEntity<PaginationResponse>> listTerritories(
            @PathVariable UUID distributorId) {
        return territoryService.listTerritories(distributorId)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create territory", description = "Create a new authorized territory for a distributor")
    public Mono<ResponseEntity<UUID>> createTerritory(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateTerritoryRequest request) {
        return territoryService.createTerritory(distributorId, request)
                .map(id -> ResponseEntity.status(HttpStatus.CREATED).body(id));
    }

    @GetMapping("/{territoryId}")
    @Operation(summary = "Get territory", description = "Retrieve a single authorized territory by its identifier")
    public Mono<ResponseEntity<TerritoryDTO>> getTerritory(
            @PathVariable UUID distributorId,
            @PathVariable UUID territoryId) {
        return territoryService.getTerritory(distributorId, territoryId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{territoryId}")
    @Operation(summary = "Update territory", description = "Update an existing authorized territory")
    public Mono<ResponseEntity<UUID>> updateTerritory(
            @PathVariable UUID distributorId,
            @PathVariable UUID territoryId,
            @Valid @RequestBody UpdateTerritoryRequest request) {
        return territoryService.updateTerritory(distributorId, territoryId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{territoryId}")
    @Operation(summary = "Delete territory", description = "Delete an authorized territory from a distributor")
    public Mono<ResponseEntity<Void>> deleteTerritory(
            @PathVariable UUID distributorId,
            @PathVariable UUID territoryId) {
        return territoryService.deleteTerritory(distributorId, territoryId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
