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


package io.fireflysoftware.experience.distributor.core.network;

import io.fireflysoftware.domain.distributor.branding.sdk.model.PaginationResponse;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AgencyDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AgencyService {

    Mono<UUID> createAgency(UUID distributorId, CreateAgencyRequest request);

    Mono<AgencyDTO> getAgency(UUID distributorId, UUID agencyId);

    Mono<PaginationResponse> listAgencies(UUID distributorId);

    Mono<UUID> updateAgency(UUID distributorId, UUID agencyId, UpdateAgencyRequest request);

    Mono<Void> deleteAgency(UUID distributorId, UUID agencyId);
}
