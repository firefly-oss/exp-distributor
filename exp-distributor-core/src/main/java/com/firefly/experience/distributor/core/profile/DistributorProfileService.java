package com.firefly.experience.distributor.core.profile;

import com.firefly.experience.distributor.interfaces.dtos.DistributorDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateDistributorRequest;
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
