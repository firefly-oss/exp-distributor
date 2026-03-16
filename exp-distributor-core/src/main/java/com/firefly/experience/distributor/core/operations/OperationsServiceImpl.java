package com.firefly.experience.distributor.core.operations;

import com.firefly.domain.distributor.branding.sdk.api.OperationApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateOperationCommand;
import com.firefly.domain.distributor.branding.sdk.model.UpdateOperationCommand;
import com.firefly.experience.distributor.interfaces.dtos.CreateOperationRequest;
import com.firefly.experience.distributor.interfaces.dtos.OperationDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateOperationRequest;
import com.firefly.experience.distributor.core.mappers.OperationMapper;
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
        return operationApi.listOperations(distributorId, UUID.randomUUID().toString())
                .map(operationMapper::toDto)
                .flux();
    }

    @Override
    public Mono<OperationDTO> createOperation(UUID distributorId, CreateOperationRequest request) {
        log.info("Creating operation for distributor: {}", distributorId);
        CreateOperationCommand command = operationMapper.toCreateCommand(request);
        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on createOperation; idempotency cannot be set at call-site.
        return operationApi.createOperation(distributorId, command, UUID.randomUUID().toString())
                .flatMap(operationId -> operationApi.getOperation(distributorId, operationId, UUID.randomUUID().toString()))
                .map(operationMapper::toDto);
    }

    @Override
    public Mono<OperationDTO> updateOperation(UUID distributorId, UUID operationId, UpdateOperationRequest request) {
        log.info("Updating operation {} for distributor: {}", operationId, distributorId);
        UpdateOperationCommand command = operationMapper.toUpdateCommand(request);
        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on updateOperation; idempotency cannot be set at call-site.
        return operationApi.updateOperation(distributorId, operationId, command, UUID.randomUUID().toString())
                .flatMap(updatedId -> operationApi.getOperation(distributorId, updatedId, UUID.randomUUID().toString()))
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
        return operationApi.canOperate(distributorId, distributorId, distributorId, UUID.randomUUID().toString());
    }
}
