package com.firefly.experience.distributor.core.profile;

import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.api.DistributorQueryApi;
import com.firefly.domain.distributor.branding.sdk.model.DistributorDTO;
import com.firefly.domain.distributor.branding.sdk.model.RegisterDistributorCommand;
import com.firefly.domain.distributor.branding.sdk.model.RegisterDistributorInfoCommand;
import com.firefly.experience.distributor.core.branding.BrandingService;
import com.firefly.experience.distributor.core.terms.TermsAndConditionsService;
import com.firefly.experience.distributor.interfaces.dtos.BrandingDTO;
import com.firefly.experience.distributor.interfaces.dtos.DistributorDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateDistributorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Composite implementation of {@link DistributorProfileService}.
 *
 * <p>Routes writes to the domain-distributor-branding command SDK and reads to
 * the domain-distributor-branding query SDK, aggregating both into a single
 * journey-specific {@link DistributorDetailDTO}.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DistributorProfileServiceImpl implements DistributorProfileService {

    /** Command-side SDK: handles distributor registration events. */
    @Qualifier("brandingDistributorApi")
    private final DistributorApi brandingDistributorApi;

    /** Query-side SDK: provides distributor profile and branding CRUD. */
    private final DistributorQueryApi distributorQueryApi;

    private final TermsAndConditionsService termsAndConditionsService;
    private final BrandingService brandingService;

    @Override
    public Mono<DistributorDetailDTO> registerDistributor(RegisterDistributorRequest request) {
        RegisterDistributorInfoCommand infoCommand = new RegisterDistributorInfoCommand();
        infoCommand.setName(request.getName());
        infoCommand.setDisplayName(request.getDisplayName());
        infoCommand.setEmail(request.getEmail());
        infoCommand.setPhoneNumber(request.getPhoneNumber());
        infoCommand.setTaxId(request.getTaxId());

        RegisterDistributorCommand sdkCommand = new RegisterDistributorCommand();
        sdkCommand.setDistributorInfo(infoCommand);

        return brandingDistributorApi.onboardDistributor(sdkCommand, UUID.randomUUID().toString())
                .map(result -> {
                    UUID distributorId = (UUID) result;
                    return DistributorDetailDTO.builder()
                            .id(distributorId)
                            .name(request.getName())
                            .displayName(request.getDisplayName())
                            .email(request.getEmail())
                            .status("ACTIVE")
                            .hasActiveSignedTerms(false)
                            .build();
                })
                .doOnNext(dto -> log.info("Registered distributor: distributorId={}", dto.getId()));
    }

    @Override
    public Mono<DistributorDetailDTO> getDistributorDetail(UUID distributorId) {
        // Fan out three independent calls concurrently, then combine
        Mono<DistributorDTO> profileMono =
                distributorQueryApi.getDistributorProfile(distributorId, null);

        Mono<Boolean> hasTermsMono =
                termsAndConditionsService.hasActiveSignedTerms(distributorId);

        Mono<BrandingDTO> activeBrandingMono = brandingService.listBrandings(distributorId)
                .filter(b -> Boolean.TRUE.equals(b.getIsDefault()))
                .next()
                .defaultIfEmpty(BrandingDTO.builder().build());

        return Mono.zip(profileMono, hasTermsMono, activeBrandingMono)
                .map(t -> {
                    DistributorDTO profile = t.getT1();
                    return DistributorDetailDTO.builder()
                            .id(distributorId)
                            .name(profile.getName())
                            .displayName(profile.getDisplayName())
                            .email(profile.getEmail())
                            .status(Boolean.TRUE.equals(profile.getIsActive()) ? "ACTIVE" : "INACTIVE")
                            .hasActiveSignedTerms(t.getT2())
                            .activeBranding(t.getT3())
                            .build();
                })
                .doOnNext(dto -> log.info("Retrieved distributor detail: distributorId={}", distributorId));
    }

    @Override
    public Mono<DistributorDetailDTO> updateDistributor(UUID distributorId, UpdateDistributorRequest request) {
        DistributorDTO updateDto = new DistributorDTO();
        updateDto.setName(request.getName());
        updateDto.setDisplayName(request.getDisplayName());
        updateDto.setEmail(request.getEmail());
        updateDto.setPhoneNumber(request.getPhoneNumber());
        updateDto.setTaxId(request.getTaxId());

        return distributorQueryApi.updateDistributor(distributorId, updateDto, UUID.randomUUID().toString())
                .flatMap(updated -> getDistributorDetail(distributorId))
                .doOnNext(dto -> log.info("Updated distributor: distributorId={}", distributorId));
    }

    @Override
    public Mono<Void> deleteDistributor(UUID distributorId) {
        return distributorQueryApi.deleteDistributor(distributorId, UUID.randomUUID().toString())
                .doOnSuccess(v -> log.info("Deleted distributor: distributorId={}", distributorId));
    }
}
