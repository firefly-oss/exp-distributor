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


package io.fireflysoftware.experience.distributor.core.profile;

import io.fireflysoftware.experience.distributor.interfaces.dtos.DistributorDetailDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateDistributorRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Experience-layer service for distributor profile operations.
 *
 * <p>Aggregates data from the domain-distributor-branding command SDK and the
 * core-common-distributor-mgmt query SDK into a single journey-specific response.
 */
public interface DistributorProfileService {

    /** Register a new distributor. Returns a composite detail view including branding and T&C status. */
    Mono<DistributorDetailDTO> registerDistributor(RegisterDistributorRequest request);

    /** Retrieve the composite distributor detail: profile + active branding + T&C status. */
    Mono<DistributorDetailDTO> getDistributorDetail(UUID distributorId);

    /** Update the distributor profile and return the refreshed composite view. */
    Mono<DistributorDetailDTO> updateDistributor(UUID distributorId, UpdateDistributorRequest request);

    /** Delete the distributor. */
    Mono<Void> deleteDistributor(UUID distributorId);
}
