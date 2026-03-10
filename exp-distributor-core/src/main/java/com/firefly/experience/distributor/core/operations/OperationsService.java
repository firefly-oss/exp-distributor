package com.firefly.experience.distributor.core.operations;

import com.firefly.experience.distributor.interfaces.dtos.CreateOperationRequest;
import com.firefly.experience.distributor.interfaces.dtos.OperationDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateOperationRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OperationsService {

    Flux<OperationDTO> listOperations(UUID distributorId);

    Mono<OperationDTO> createOperation(UUID distributorId, CreateOperationRequest request);

    Mono<OperationDTO> updateOperation(UUID distributorId, UUID operationId, UpdateOperationRequest request);

    Mono<Void> deleteOperation(UUID distributorId, UUID operationId);

    Mono<OperationDTO> activateOperation(UUID distributorId, UUID operationId);

    Mono<OperationDTO> deactivateOperation(UUID distributorId, UUID operationId);

    Mono<Boolean> canOperate(UUID distributorId);
}
