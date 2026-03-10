package com.firefly.experience.distributor.web.controllers;

import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.experience.distributor.core.network.AgencyService;
import com.firefly.experience.distributor.interfaces.dtos.AgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
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
@RequestMapping("/api/v1/experience/distributors/{distributorId}/agencies")
@RequiredArgsConstructor
@Tag(name = "Agencies", description = "Manage distributor agencies")
public class AgencyController {

    private final AgencyService agencyService;

    @GetMapping
    @Operation(summary = "List agencies", description = "List all agencies for a distributor")
    public Mono<ResponseEntity<PaginationResponse>> listAgencies(
            @PathVariable UUID distributorId) {
        return agencyService.listAgencies(distributorId)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create agency", description = "Create a new agency for a distributor")
    public Mono<ResponseEntity<UUID>> createAgency(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateAgencyRequest request) {
        return agencyService.createAgency(distributorId, request)
                .map(id -> ResponseEntity.status(HttpStatus.CREATED).body(id));
    }

    @GetMapping("/{agencyId}")
    @Operation(summary = "Get agency", description = "Retrieve a single agency by its identifier")
    public Mono<ResponseEntity<AgencyDTO>> getAgency(
            @PathVariable UUID distributorId,
            @PathVariable UUID agencyId) {
        return agencyService.getAgency(distributorId, agencyId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{agencyId}")
    @Operation(summary = "Update agency", description = "Update an existing agency")
    public Mono<ResponseEntity<UUID>> updateAgency(
            @PathVariable UUID distributorId,
            @PathVariable UUID agencyId,
            @Valid @RequestBody UpdateAgencyRequest request) {
        return agencyService.updateAgency(distributorId, agencyId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{agencyId}")
    @Operation(summary = "Delete agency", description = "Delete an agency from a distributor")
    public Mono<ResponseEntity<Void>> deleteAgency(
            @PathVariable UUID distributorId,
            @PathVariable UUID agencyId) {
        return agencyService.deleteAgency(distributorId, agencyId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
