package com.firefly.experience.distributor.core.network;

import com.firefly.domain.distributor.branding.sdk.api.AgencyApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateAgencyCommand;
import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.domain.distributor.branding.sdk.model.UpdateAgencyCommand;
import com.firefly.experience.distributor.interfaces.dtos.AgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
import com.firefly.experience.distributor.core.mappers.AgencyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final AgencyApi agencyApi;
    private final AgencyMapper agencyMapper;

    @Override
    public Mono<UUID> createAgency(UUID distributorId, CreateAgencyRequest request) {
        log.info("Creating agency for distributor: {}", distributorId);

        CreateAgencyCommand command = agencyMapper.toCreateCommand(request);

        return agencyApi.createAgency(distributorId, command);
    }

    @Override
    public Mono<AgencyDTO> getAgency(UUID distributorId, UUID agencyId) {
        log.info("Getting agency {} for distributor: {}", agencyId, distributorId);
        return agencyApi.getAgency(distributorId, agencyId)
                .map(agencyMapper::toDto);
    }

    @Override
    public Mono<PaginationResponse> listAgencies(UUID distributorId) {
        log.info("Listing agencies for distributor: {}", distributorId);
        return agencyApi.listAgencies(distributorId);
    }

    @Override
    public Mono<UUID> updateAgency(UUID distributorId, UUID agencyId, UpdateAgencyRequest request) {
        log.info("Updating agency {} for distributor: {}", agencyId, distributorId);

        UpdateAgencyCommand command = agencyMapper.toUpdateCommand(request);

        return agencyApi.updateAgency(distributorId, agencyId, command);
    }

    @Override
    public Mono<Void> deleteAgency(UUID distributorId, UUID agencyId) {
        log.info("Deleting agency {} for distributor: {}", agencyId, distributorId);
        return agencyApi.deleteAgency(distributorId, agencyId);
    }
}
