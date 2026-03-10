package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.terms.TermsAndConditionsService;
import com.firefly.experience.distributor.interfaces.dtos.CreateTermsRequest;
import com.firefly.experience.distributor.interfaces.dtos.TermsDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/v1/experience/distributors/{distributorId}/terms-and-conditions")
@RequiredArgsConstructor
@Tag(name = "Terms and Conditions", description = "Terms and conditions management operations")
public class TermsAndConditionsController {

    private final TermsAndConditionsService termsAndConditionsService;

    @GetMapping
    @Operation(summary = "List terms and conditions", description = "List all terms and conditions for a distributor")
    public Mono<ResponseEntity<List<TermsDTO>>> listTerms(@PathVariable UUID distributorId) {
        return termsAndConditionsService.listTerms(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active terms", description = "Get all active terms and conditions for a distributor")
    public Mono<ResponseEntity<List<TermsDTO>>> getActiveTerms(@PathVariable UUID distributorId) {
        return termsAndConditionsService.getActiveTerms(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest terms", description = "Get the latest terms and conditions for a distributor")
    public Mono<ResponseEntity<TermsDTO>> getLatestTerms(@PathVariable UUID distributorId) {
        return termsAndConditionsService.getLatestTerms(distributorId)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create terms and conditions", description = "Create new terms and conditions for a distributor")
    public Mono<ResponseEntity<TermsDTO>> createTerms(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateTermsRequest request) {
        return termsAndConditionsService.createTerms(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @GetMapping("/{tcId}")
    @Operation(summary = "Get terms detail", description = "Get terms and conditions detail by ID")
    public Mono<ResponseEntity<TermsDTO>> getTermsDetail(
            @PathVariable UUID distributorId,
            @PathVariable UUID tcId) {
        return termsAndConditionsService.getTermsDetail(distributorId, tcId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{tcId}")
    @Operation(summary = "Update terms and conditions", description = "Update existing terms and conditions")
    public Mono<ResponseEntity<TermsDTO>> updateTerms(
            @PathVariable UUID distributorId,
            @PathVariable UUID tcId,
            @Valid @RequestBody UpdateTermsRequest request) {
        return termsAndConditionsService.updateTerms(distributorId, tcId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{tcId}")
    @Operation(summary = "Delete terms and conditions", description = "Delete terms and conditions by ID")
    public Mono<ResponseEntity<Void>> deleteTerms(
            @PathVariable UUID distributorId,
            @PathVariable UUID tcId) {
        return termsAndConditionsService.deleteTerms(distributorId, tcId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @PatchMapping("/{tcId}/sign")
    @Operation(summary = "Sign terms and conditions", description = "Sign terms and conditions for a distributor")
    public Mono<ResponseEntity<TermsDTO>> signTerms(
            @PathVariable UUID distributorId,
            @PathVariable UUID tcId) {
        return termsAndConditionsService.signTerms(distributorId, tcId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{tcId}/activate")
    @Operation(summary = "Activate terms and conditions", description = "Activate terms and conditions for a distributor")
    public Mono<ResponseEntity<TermsDTO>> activateTerms(
            @PathVariable UUID distributorId,
            @PathVariable UUID tcId) {
        return termsAndConditionsService.activateTerms(distributorId, tcId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{tcId}/deactivate")
    @Operation(summary = "Deactivate terms and conditions", description = "Deactivate terms and conditions for a distributor")
    public Mono<ResponseEntity<TermsDTO>> deactivateTerms(
            @PathVariable UUID distributorId,
            @PathVariable UUID tcId) {
        return termsAndConditionsService.deactivateTerms(distributorId, tcId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/has-active-signed")
    @Operation(summary = "Check active signed terms", description = "Check if the distributor has active signed terms and conditions")
    public Mono<ResponseEntity<Boolean>> hasActiveSignedTerms(@PathVariable UUID distributorId) {
        return termsAndConditionsService.hasActiveSignedTerms(distributorId)
                .map(ResponseEntity::ok);
    }
}
