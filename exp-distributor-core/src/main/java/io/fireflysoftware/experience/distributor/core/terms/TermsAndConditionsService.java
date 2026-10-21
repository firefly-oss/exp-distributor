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


package io.fireflysoftware.experience.distributor.core.terms;

import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateTermsRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.TermsDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TermsAndConditionsService {

    Flux<TermsDTO> listTerms(UUID distributorId);

    Flux<TermsDTO> getActiveTerms(UUID distributorId);

    Mono<TermsDTO> getLatestTerms(UUID distributorId);

    Mono<TermsDTO> createTerms(UUID distributorId, CreateTermsRequest request);

    Mono<TermsDTO> getTermsDetail(UUID distributorId, UUID tcId);

    Mono<TermsDTO> updateTerms(UUID distributorId, UUID tcId, UpdateTermsRequest request);

    Mono<Void> deleteTerms(UUID distributorId, UUID tcId);

    Mono<TermsDTO> signTerms(UUID distributorId, UUID tcId);

    Mono<TermsDTO> activateTerms(UUID distributorId, UUID tcId);

    Mono<TermsDTO> deactivateTerms(UUID distributorId, UUID tcId);

    Mono<Boolean> hasActiveSignedTerms(UUID distributorId);
}
