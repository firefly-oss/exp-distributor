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

import io.fireflysoftware.experience.distributor.core.operations.OperationsService;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateOperationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.OperationDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateOperationRequest;
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
@RequestMapping("/api/v1/experience/distributors/{distributorId}/operations")
@RequiredArgsConstructor
@Tag(name = "Operations", description = "Distributor operations management")
public class OperationsController {

    private final OperationsService operationsService;

    @GetMapping
    @Operation(summary = "List operations", description = "List all operations for a distributor")
    public Mono<ResponseEntity<List<OperationDTO>>> listOperations(@PathVariable UUID distributorId) {
        return operationsService.listOperations(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create operation", description = "Create a new operation for a distributor")
    public Mono<ResponseEntity<OperationDTO>> createOperation(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateOperationRequest request) {
        return operationsService.createOperation(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @PutMapping("/{operationId}")
    @Operation(summary = "Update operation", description = "Update an existing operation")
    public Mono<ResponseEntity<OperationDTO>> updateOperation(
            @PathVariable UUID distributorId,
            @PathVariable UUID operationId,
            @Valid @RequestBody UpdateOperationRequest request) {
        return operationsService.updateOperation(distributorId, operationId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{operationId}")
    @Operation(summary = "Delete operation", description = "Delete an operation by ID")
    public Mono<ResponseEntity<Void>> deleteOperation(
            @PathVariable UUID distributorId,
            @PathVariable UUID operationId) {
        return operationsService.deleteOperation(distributorId, operationId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @PatchMapping("/{operationId}/activate")
    @Operation(summary = "Activate operation", description = "Activate an operation for a distributor")
    public Mono<ResponseEntity<OperationDTO>> activateOperation(
            @PathVariable UUID distributorId,
            @PathVariable UUID operationId) {
        return operationsService.activateOperation(distributorId, operationId)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{operationId}/deactivate")
    @Operation(summary = "Deactivate operation", description = "Deactivate an operation for a distributor")
    public Mono<ResponseEntity<OperationDTO>> deactivateOperation(
            @PathVariable UUID distributorId,
            @PathVariable UUID operationId) {
        return operationsService.deactivateOperation(distributorId, operationId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/can-operate")
    @Operation(summary = "Check can operate", description = "Check if the distributor can operate")
    public Mono<ResponseEntity<Boolean>> canOperate(@PathVariable UUID distributorId) {
        return operationsService.canOperate(distributorId)
                .map(ResponseEntity::ok);
    }
}
