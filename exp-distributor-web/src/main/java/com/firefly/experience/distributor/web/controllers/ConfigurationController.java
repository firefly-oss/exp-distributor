package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.config.ConfigurationService;
import com.firefly.experience.distributor.interfaces.dtos.ConfigurationDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
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
@RequestMapping("/api/v1/experience/distributors/{distributorId}/configurations")
@RequiredArgsConstructor
@Tag(name = "Configurations", description = "Distributor configuration management")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    @Operation(summary = "List configurations", description = "List all configurations for a distributor")
    public Mono<ResponseEntity<List<ConfigurationDTO>>> listConfigurations(@PathVariable UUID distributorId) {
        return configurationService.listConfigurations(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create configuration", description = "Create a new configuration for a distributor")
    public Mono<ResponseEntity<ConfigurationDTO>> createConfiguration(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateConfigurationRequest request) {
        return configurationService.createConfiguration(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @PutMapping("/{configId}")
    @Operation(summary = "Update configuration", description = "Update an existing configuration")
    public Mono<ResponseEntity<ConfigurationDTO>> updateConfiguration(
            @PathVariable UUID distributorId,
            @PathVariable UUID configId,
            @Valid @RequestBody UpdateConfigurationRequest request) {
        return configurationService.updateConfiguration(distributorId, configId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "Delete configuration", description = "Delete a configuration by ID")
    public Mono<ResponseEntity<Void>> deleteConfiguration(
            @PathVariable UUID distributorId,
            @PathVariable UUID configId) {
        return configurationService.deleteConfiguration(distributorId, configId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
