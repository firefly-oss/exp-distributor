package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.profile.DistributorProfileService;
import com.firefly.experience.distributor.interfaces.dtos.DistributorDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateDistributorRequest;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors")
@RequiredArgsConstructor
@Tag(name = "Distributor Profile", description = "Distributor profile management operations")
public class DistributorProfileController {

    private final DistributorProfileService distributorProfileService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register distributor",
               description = "Onboard a new distributor and return the composite profile view")
    public Mono<ResponseEntity<DistributorDetailDTO>> registerDistributor(
            @Valid @RequestBody RegisterDistributorRequest request) {
        return distributorProfileService.registerDistributor(request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping(value = "/{distributorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get distributor detail",
               description = "Retrieve composite distributor detail: profile + active branding + T&C status")
    public Mono<ResponseEntity<DistributorDetailDTO>> getDistributorDetail(
            @PathVariable UUID distributorId) {
        return distributorProfileService.getDistributorDetail(distributorId)
                .map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{distributorId}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update distributor",
               description = "Update the distributor profile and return the refreshed composite view")
    public Mono<ResponseEntity<DistributorDetailDTO>> updateDistributor(
            @PathVariable UUID distributorId,
            @Valid @RequestBody UpdateDistributorRequest request) {
        return distributorProfileService.updateDistributor(distributorId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/{distributorId}")
    @Operation(summary = "Delete distributor",
               description = "Delete a distributor by ID")
    public Mono<ResponseEntity<Void>> deleteDistributor(
            @PathVariable UUID distributorId) {
        return distributorProfileService.deleteDistributor(distributorId)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}
