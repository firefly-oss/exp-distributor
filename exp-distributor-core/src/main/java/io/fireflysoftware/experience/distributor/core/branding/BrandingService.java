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


package io.fireflysoftware.experience.distributor.core.branding;

import io.fireflysoftware.experience.distributor.interfaces.dtos.BrandingDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateBrandingRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Experience-layer service for distributor branding operations.
 *
 * <p>Combines write commands via the domain-distributor-branding SDK with read
 * queries via the core-common-distributor-mgmt SDK.
 */
public interface BrandingService {

    /** List all branding configurations for the given distributor. */
    Flux<BrandingDTO> listBrandings(UUID distributorId);

    /** Create a new branding configuration for the given distributor. */
    Mono<BrandingDTO> createBranding(UUID distributorId, CreateBrandingRequest request);

    /** Retrieve a single branding configuration by ID. */
    Mono<BrandingDTO> getBranding(UUID distributorId, UUID brandingId);

    /** Update an existing branding configuration. */
    Mono<BrandingDTO> updateBranding(UUID distributorId, UUID brandingId, UpdateBrandingRequest request);

    /** Delete a branding configuration. */
    Mono<Void> deleteBranding(UUID distributorId, UUID brandingId);

    /** Mark a branding configuration as the default for its distributor. */
    Mono<BrandingDTO> setDefaultBranding(UUID distributorId, UUID brandingId);
}
