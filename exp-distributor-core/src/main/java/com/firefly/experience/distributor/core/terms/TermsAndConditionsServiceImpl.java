package com.firefly.experience.distributor.core.terms;

import com.firefly.domain.distributor.branding.sdk.api.TermsAndConditionsApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateTermsAndConditionsCommand;
import com.firefly.experience.distributor.interfaces.dtos.CreateTermsRequest;
import com.firefly.experience.distributor.interfaces.dtos.TermsDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import com.firefly.experience.distributor.core.mappers.TermsMapper;
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
        return termsAndConditionsApi.listTermsAndConditions(distributorId)
                .map(termsMapper::toDto)
                .flux();
    }

    @Override
    public Flux<TermsDTO> getActiveTerms(UUID distributorId) {
        log.info("Getting active terms and conditions for distributor: {}", distributorId);
        return termsAndConditionsApi.getActiveTermsAndConditions(distributorId)
                .map(termsMapper::toDto)
                .flux();
    }

    @Override
    public Mono<TermsDTO> getLatestTerms(UUID distributorId) {
        log.info("Getting latest terms and conditions for distributor: {}", distributorId);
        return termsAndConditionsApi.getLatestTermsAndConditions(distributorId)
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> createTerms(UUID distributorId, CreateTermsRequest request) {
        log.info("Creating terms and conditions for distributor: {}", distributorId);
        CreateTermsAndConditionsCommand command = termsMapper.toCreateCommand(request);
        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on createTermsAndConditions; idempotency cannot be set at call-site.
        return termsAndConditionsApi.createTermsAndConditions(distributorId, command)
                .flatMap(tcId -> termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, tcId))
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> getTermsDetail(UUID distributorId, UUID tcId) {
        log.info("Getting terms and conditions detail {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, tcId)
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> updateTerms(UUID distributorId, UUID tcId, UpdateTermsRequest request) {
        log.info("Updating terms and conditions {} for distributor: {}", tcId, distributorId);
        CreateTermsAndConditionsCommand command = termsMapper.toUpdateCommand(request);
        return termsAndConditionsApi.deleteTermsAndConditions(distributorId, tcId)
                .then(termsAndConditionsApi.createTermsAndConditions(distributorId, command))
                .flatMap(newTcId -> termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, newTcId))
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<Void> deleteTerms(UUID distributorId, UUID tcId) {
        log.info("Deleting terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.deleteTermsAndConditions(distributorId, tcId);
    }

    @Override
    public Mono<TermsDTO> signTerms(UUID distributorId, UUID tcId) {
        log.info("Signing terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.signTermsAndConditions(distributorId, tcId, UUID.randomUUID())
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> activateTerms(UUID distributorId, UUID tcId) {
        log.info("Activating terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.activateTermsAndConditions(distributorId, tcId, UUID.randomUUID())
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<TermsDTO> deactivateTerms(UUID distributorId, UUID tcId) {
        log.info("Deactivating terms and conditions {} for distributor: {}", tcId, distributorId);
        return termsAndConditionsApi.deactivateTermsAndConditions(distributorId, tcId, UUID.randomUUID())
                .map(termsMapper::toDto);
    }

    @Override
    public Mono<Boolean> hasActiveSignedTerms(UUID distributorId) {
        log.info("Checking if distributor {} has active signed terms", distributorId);
        return termsAndConditionsApi.hasActiveSignedTerms(distributorId);
    }

}
