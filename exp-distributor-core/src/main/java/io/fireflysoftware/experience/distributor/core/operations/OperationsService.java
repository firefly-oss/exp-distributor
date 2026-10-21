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

import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateOperationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.OperationDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateOperationRequest;
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
