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

import io.fireflysoftware.domain.distributor.branding.sdk.api.AgencyApi;
import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateAgencyCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateAgencyCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AgencyDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
import io.fireflysoftware.experience.distributor.core.mappers.AgencyMapper;
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

        return agencyApi.createAgency(distributorId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<AgencyDTO> getAgency(UUID distributorId, UUID agencyId) {
        log.info("Getting agency {} for distributor: {}", agencyId, distributorId);
        return agencyApi.getAgency(distributorId, agencyId, null)
                .map(agencyMapper::toDto);
    }

    @Override
    public Mono<PaginationResponse> listAgencies(UUID distributorId) {
        log.info("Listing agencies for distributor: {}", distributorId);
        return agencyApi.listAgencies(distributorId, null);
    }

    @Override
    public Mono<UUID> updateAgency(UUID distributorId, UUID agencyId, UpdateAgencyRequest request) {
        log.info("Updating agency {} for distributor: {}", agencyId, distributorId);

        UpdateAgencyCommand command = agencyMapper.toUpdateCommand(request);

        return agencyApi.updateAgency(distributorId, agencyId, command, UUID.randomUUID().toString());
    }

    @Override
    public Mono<Void> deleteAgency(UUID distributorId, UUID agencyId) {
        log.info("Deleting agency {} for distributor: {}", agencyId, distributorId);
        return agencyApi.deleteAgency(distributorId, agencyId, UUID.randomUUID().toString());
    }
}
