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

import io.fireflysoftware.domain.distributor.branding.sdk.api.TermsAndConditionsApi;
import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateTermsAndConditionsCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateTermsRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.TermsDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import io.fireflysoftware.experience.distributor.core.mappers.TermsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermsAndConditionsServiceImpl implements TermsAndConditionsService {

    private final TermsAndConditionsApi termsAndConditionsApi;
    private final TermsMapper termsMapper;

    @Override
    public Flux<TermsDTO> listTerms(UUID distributorId) {
        log.info("Listing terms and conditions for distributor: {}", distributorId);
        return termsAndConditionsApi.listTermsAndConditions(distributorId, null)
                .map(termsMapper::toDto)
                .flux();
    }

    @Override
    public Flux<TermsDTO> getActiveTerms(UUID distributorId) {
        log.info("Getting active terms and conditions for distributor: {}", distributorId);
        return termsAndConditionsApi.getActiveTermsAndConditions(distributorId, null)
                .map(termsMapper::toDto)
                .flux();
    }

    @Override
    public Mono<TermsDTO> getLatestTerms(UUID distributorId) {
        log.info("Getting latest terms and conditions for distributor: {}", distributorId);
        return termsAndConditionsApi.getLatestTermsAndConditions(distributorId, null)
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> createTerms(UUID distributorId, CreateTermsRequest request) {
        log.info("Creating terms and conditions for distributor: {}", distributorId);
        CreateTermsAndConditionsCommand command = termsMapper.toCreateCommand(request);
        return termsAndConditionsApi.createTermsAndConditions(distributorId, command, UUID.randomUUID().toString())
                .flatMap(tcId -> termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, tcId, null))
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> getTermsDetail(UUID distributorId, UUID tcId) {
        log.info("Getting terms and conditions detail {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, tcId, null)
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> updateTerms(UUID distributorId, UUID tcId, UpdateTermsRequest request) {
        log.info("Updating terms and conditions {} for distributor: {}", tcId, distributorId);
        CreateTermsAndConditionsCommand command = termsMapper.toUpdateCommand(request);
        return termsAndConditionsApi.deleteTermsAndConditions(distributorId, tcId, UUID.randomUUID().toString())
                .then(termsAndConditionsApi.createTermsAndConditions(distributorId, command, UUID.randomUUID().toString()))
                .flatMap(newTcId -> termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, newTcId, null))
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<Void> deleteTerms(UUID distributorId, UUID tcId) {
        log.info("Deleting terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.deleteTermsAndConditions(distributorId, tcId, UUID.randomUUID().toString());
    }

    @Override
    public Mono<TermsDTO> signTerms(UUID distributorId, UUID tcId) {
        log.info("Signing terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.signTermsAndConditions(distributorId, tcId, UUID.randomUUID(), UUID.randomUUID().toString())
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> activateTerms(UUID distributorId, UUID tcId) {
        log.info("Activating terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.activateTermsAndConditions(distributorId, tcId, UUID.randomUUID(), UUID.randomUUID().toString())
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> deactivateTerms(UUID distributorId, UUID tcId) {
        log.info("Deactivating terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.deactivateTermsAndConditions(distributorId, tcId, UUID.randomUUID(), UUID.randomUUID().toString())
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<Boolean> hasActiveSignedTerms(UUID distributorId) {
        log.info("Checking if distributor {} has active signed terms", distributorId);
        return termsAndConditionsApi.hasActiveSignedTerms(distributorId, null);
    }

}
