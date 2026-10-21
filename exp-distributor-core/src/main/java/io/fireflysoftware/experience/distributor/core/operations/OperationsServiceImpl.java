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


package io.fireflysoftware.experience.distributor.core.operations;

import io.fireflysoftware.domain.distributor.branding.sdk.api.OperationApi;
import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateOperationCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateOperationCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateOperationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.OperationDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateOperationRequest;
import io.fireflysoftware.experience.distributor.core.mappers.OperationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationsServiceImpl implements OperationsService {

    private final OperationApi operationApi;
    private final OperationMapper operationMapper;

    @Override
    public Flux<OperationDTO> listOperations(UUID distributorId) {
        log.info("Listing operations for distributor: {}", distributorId);
        return operationApi.listOperations(distributorId, null)
                .map(operationMapper::toDto)
                .flux();
    }

    @Override
    public Mono<OperationDTO> createOperation(UUID distributorId, CreateOperationRequest request) {
        log.info("Creating operation for distributor: {}", distributorId);
        CreateOperationCommand command = operationMapper.toCreateCommand(request);
        return operationApi.createOperation(distributorId, command, UUID.randomUUID().toString())
                .flatMap(operationId -> operationApi.getOperation(distributorId, operationId, null))
                .map(operationMapper::toDto);
    }

    @Override
    public Mono<OperationDTO> updateOperation(UUID distributorId, UUID operationId, UpdateOperationRequest request) {
        log.info("Updating operation {} for distributor: {}", operationId, distributorId);
        UpdateOperationCommand command = operationMapper.toUpdateCommand(request);
        return operationApi.updateOperation(distributorId, operationId, command, UUID.randomUUID().toString())
                .flatMap(updatedId -> operationApi.getOperation(distributorId, updatedId, null))
                .map(operationMapper::toDto);
    }

    @Override
    public Mono<Void> deleteOperation(UUID distributorId, UUID operationId) {
        log.info("Deleting operation {} for distributor: {}", operationId, distributorId);
        return operationApi.deleteOperation(distributorId, operationId, UUID.randomUUID().toString());
    }

    @Override
    public Mono<OperationDTO> activateOperation(UUID distributorId, UUID operationId) {
        log.info("Activating operation {} for distributor: {}", operationId, distributorId);
        return operationApi.activateOperation(distributorId, operationId, UUID.randomUUID(), UUID.randomUUID().toString())
                .map(operationMapper::toDto);
    }

    @Override
    public Mono<OperationDTO> deactivateOperation(UUID distributorId, UUID operationId) {
        log.info("Deactivating operation {} for distributor: {}", operationId, distributorId);
        return operationApi.deactivateOperation(distributorId, operationId, UUID.randomUUID(), UUID.randomUUID().toString())
                .map(operationMapper::toDto);
    }

    @Override
    public Mono<Boolean> canOperate(UUID distributorId) {
        log.info("Checking if distributor {} can operate", distributorId);
        return operationApi.canOperate(distributorId, distributorId, distributorId, null);
    }
}
