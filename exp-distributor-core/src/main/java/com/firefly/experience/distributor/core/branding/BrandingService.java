package com.firefly.experience.distributor.core.branding;

import com.firefly.experience.distributor.interfaces.dtos.BrandingDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateBrandingRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
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
