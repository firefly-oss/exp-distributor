package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.branding.BrandingService;
import com.firefly.experience.distributor.interfaces.dtos.BrandingDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateBrandingRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/experience/distributors/{distributorId}/brandings")
@RequiredArgsConstructor
@Tag(name = "Branding", description = "Distributor branding management operations")
public class BrandingController {

    private final BrandingService brandingService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List brandings",
               description = "List all branding configurations for a distributor")
    public Mono<ResponseEntity<List<BrandingDTO>>> listBrandings(
            @PathVariable UUID distributorId) {
        return brandingService.listBrandings(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create branding",
               description = "Create a new branding configuration for a distributor")
    public Mono<ResponseEntity<BrandingDTO>> createBranding(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateBrandingRequest request) {
        return brandingService.createBranding(distributorId, request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping(value = "/{brandingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get branding",
               description = "Retrieve a branding configuration by ID")
    public Mono<ResponseEntity<BrandingDTO>> getBranding(
            @PathVariable UUID distributorId,
            @PathVariable UUID brandingId) {
        return brandingService.getBranding(distributorId, brandingId)
                .map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{brandingId}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update branding",
               description = "Update an existing branding configuration")
    public Mono<ResponseEntity<BrandingDTO>> updateBranding(
            @PathVariable UUID distributorId,
            @PathVariable UUID brandingId,
            @Valid @RequestBody UpdateBrandingRequest request) {
        return brandingService.updateBranding(distributorId, brandingId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/{brandingId}")
    @Operation(summary = "Delete branding",
               description = "Delete a branding configuration by ID")
    public Mono<ResponseEntity<Void>> deleteBranding(
            @PathVariable UUID distributorId,
            @PathVariable UUID brandingId) {
        return brandingService.deleteBranding(distributorId, brandingId)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }

    @PutMapping(value = "/{brandingId}/set-default",
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Set default branding",
               description = "Mark a branding configuration as the default for the distributor")
    public Mono<ResponseEntity<BrandingDTO>> setDefaultBranding(
            @PathVariable UUID distributorId,
            @PathVariable UUID brandingId) {
        return brandingService.setDefaultBranding(distributorId, brandingId)
                .map(ResponseEntity::ok);
    }
}
