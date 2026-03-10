package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.profile.DistributorProfileService;
import com.firefly.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    @Operation(summary = "Register distributor", description = "Register a new distributor in the platform")
    public Mono<ResponseEntity<UUID>> registerDistributor(
            @Valid @RequestBody RegisterDistributorRequest request) {
        return distributorProfileService.registerDistributor(request)
                .map(id -> ResponseEntity.status(HttpStatus.CREATED).body(id));
    }
}
