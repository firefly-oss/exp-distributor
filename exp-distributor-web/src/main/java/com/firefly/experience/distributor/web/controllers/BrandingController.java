package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.branding.BrandingService;
import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/brandings")
@RequiredArgsConstructor
@Tag(name = "Branding", description = "Distributor branding management operations")
public class BrandingController {

    private final BrandingService brandingService;

    @PutMapping("/{brandingId}")
    @Operation(summary = "Revise branding", description = "Revise an existing branding configuration")
    public Mono<ResponseEntity<UUID>> reviseBranding(
            @PathVariable UUID distributorId,
            @PathVariable UUID brandingId,
            @Valid @RequestBody UpdateBrandingRequest request) {
        return brandingService.reviseBranding(distributorId, brandingId, request)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{brandingId}/set-default")
    @Operation(summary = "Set default branding", description = "Set a branding configuration as the default for a distributor")
    public Mono<ResponseEntity<UUID>> setDefaultBranding(
            @PathVariable UUID distributorId,
            @PathVariable UUID brandingId) {
        return brandingService.setDefaultBranding(distributorId, brandingId)
                .map(ResponseEntity::ok);
    }
}
