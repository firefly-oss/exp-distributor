package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.catalog.CatalogService;
import com.firefly.experience.distributor.interfaces.dtos.AddCatalogItemRequest;
import com.firefly.experience.distributor.interfaces.dtos.CatalogItemDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateCatalogItemRequest;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Distributor catalog management operations")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping
    @Operation(summary = "List catalog items", description = "List all catalog items for a distributor")
    public Mono<ResponseEntity<List<CatalogItemDTO>>> listCatalog(@PathVariable UUID distributorId) {
        return catalogService.listCatalog(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Add to catalog", description = "Add a new item to the distributor's catalog")
    public Mono<ResponseEntity<UUID>> addToCatalog(
            @PathVariable UUID distributorId,
            @Valid @RequestBody AddCatalogItemRequest request) {
        return catalogService.addToCatalog(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @GetMapping("/{catalogItemId}")
    @Operation(summary = "Get catalog item", description = "Retrieve a specific catalog item by ID")
    public Mono<ResponseEntity<CatalogItemDTO>> getCatalogItem(
            @PathVariable UUID distributorId,
            @PathVariable UUID catalogItemId) {
        return catalogService.getCatalogItem(distributorId, catalogItemId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{catalogItemId}")
    @Operation(summary = "Update catalog item", description = "Update an existing catalog item")
    public Mono<ResponseEntity<UUID>> updateCatalogItem(
            @PathVariable UUID distributorId,
            @PathVariable UUID catalogItemId,
            @Valid @RequestBody UpdateCatalogItemRequest request) {
        return catalogService.updateCatalogItem(distributorId, catalogItemId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{catalogItemId}")
    @Operation(summary = "Remove from catalog", description = "Remove an item from the distributor's catalog")
    public Mono<ResponseEntity<Void>> removeFromCatalog(
            @PathVariable UUID distributorId,
            @PathVariable UUID catalogItemId) {
        return catalogService.removeFromCatalog(distributorId, catalogItemId)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }
}
