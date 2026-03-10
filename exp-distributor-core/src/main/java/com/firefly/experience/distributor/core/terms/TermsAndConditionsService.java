package com.firefly.experience.distributor.core.terms;

import com.firefly.experience.distributor.interfaces.dtos.CreateTermsRequest;
import com.firefly.experience.distributor.interfaces.dtos.TermsDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TermsAndConditionsService {

    Flux<TermsDTO> listTerms(UUID distributorId);

    Flux<TermsDTO> getActiveTerms(UUID distributorId);

    Mono<TermsDTO> getLatestTerms(UUID distributorId);

    Mono<TermsDTO> createTerms(UUID distributorId, CreateTermsRequest request);

    Mono<TermsDTO> getTermsDetail(UUID distributorId, UUID tcId);

    Mono<TermsDTO> updateTerms(UUID distributorId, UUID tcId, UpdateTermsRequest request);

    Mono<Void> deleteTerms(UUID distributorId, UUID tcId);

    Mono<TermsDTO> signTerms(UUID distributorId, UUID tcId);

    Mono<TermsDTO> activateTerms(UUID distributorId, UUID tcId);

    Mono<TermsDTO> deactivateTerms(UUID distributorId, UUID tcId);

    Mono<Boolean> hasActiveSignedTerms(UUID distributorId);
}
