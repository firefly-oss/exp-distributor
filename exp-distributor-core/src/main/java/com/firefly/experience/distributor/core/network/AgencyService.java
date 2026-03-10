package com.firefly.experience.distributor.core.network;

import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.experience.distributor.interfaces.dtos.AgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AgencyService {

    Mono<UUID> createAgency(UUID distributorId, CreateAgencyRequest request);

    Mono<AgencyDTO> getAgency(UUID distributorId, UUID agencyId);

    Mono<PaginationResponse> listAgencies(UUID distributorId);

    Mono<UUID> updateAgency(UUID distributorId, UUID agencyId, UpdateAgencyRequest request);

    Mono<Void> deleteAgency(UUID distributorId, UUID agencyId);
}
