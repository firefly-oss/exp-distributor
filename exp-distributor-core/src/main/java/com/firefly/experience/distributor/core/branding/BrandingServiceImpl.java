package com.firefly.experience.distributor.core.branding;

import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.api.DistributorQueryApi;
import com.firefly.domain.distributor.branding.sdk.model.DistributorBrandingDTO;
import com.firefly.domain.distributor.branding.sdk.model.FilterRequestDistributorBrandingDTO;
import com.firefly.domain.distributor.branding.sdk.model.ReviseBrandingCommand;
import com.firefly.domain.distributor.branding.sdk.model.SetDefaultBrandingCommand;
import com.firefly.experience.distributor.interfaces.dtos.BrandingDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateBrandingRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementation of {@link BrandingService}.
 *
 * <p>Writes (create, update, setDefault) delegate to the domain-distributor-branding
 * command SDK where applicable. Reads (list, get) and delete use the
 * domain-distributor-branding query SDK's {@link DistributorQueryApi}.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BrandingServiceImpl implements BrandingService {

    /** Command-side SDK: handles branding write events. */
    @Qualifier("brandingDistributorApi")
    private final DistributorApi brandingDistributorApi;

    /** Query-side SDK: full CRUD for distributor branding. */
    private final DistributorQueryApi distributorQueryApi;

    @Override
    public Flux<BrandingDTO> listBrandings(UUID distributorId) {
        log.info("Listing brandings for distributor: {}", distributorId);
        return distributorQueryApi
                .filterDistributorBrandingsWithResponseSpec(
                        distributorId, new FilterRequestDistributorBrandingDTO(), null)
                .bodyToFlux(DistributorBrandingDTO.class)
                .map(this::toDTO);
    }

    @Override
    public Mono<BrandingDTO> createBranding(UUID distributorId, CreateBrandingRequest request) {
        log.info("Creating branding for distributor: {}", distributorId);
        DistributorBrandingDTO dto = new DistributorBrandingDTO();
        dto.setDistributorId(distributorId);
        dto.setLogoUrl(request.getLogoUrl());
        dto.setPrimaryColor(request.getPrimaryColor());
        dto.setSecondaryColor(request.getSecondaryColor());
        dto.setFontFamily(request.getFontFamily());
        if (request.getTheme() != null) {
            dto.setTheme(DistributorBrandingDTO.ThemeEnum.fromValue(request.getTheme()));
        }
        return distributorQueryApi.createDistributorBranding(distributorId, dto, UUID.randomUUID().toString())
                .map(this::toDTO)
                .doOnNext(b -> log.info("Created branding: distributorId={}, brandingId={}",
                        distributorId, b.getId()));
    }

    @Override
    public Mono<BrandingDTO> getBranding(UUID distributorId, UUID brandingId) {
        log.info("Getting branding {} for distributor: {}", brandingId, distributorId);
        return distributorQueryApi.getDistributorBranding(distributorId, brandingId, null)
                .map(this::toDTO);
    }

    @Override
    public Mono<BrandingDTO> updateBranding(UUID distributorId, UUID brandingId, UpdateBrandingRequest request) {
        log.info("Updating branding {} for distributor: {}", brandingId, distributorId);
        // Write the change via the command SDK (domain event)
        ReviseBrandingCommand command = new ReviseBrandingCommand();
        command.setLogoUrl(request.getLogoUrl());
        command.setPrimaryColor(request.getPrimaryColor());
        command.setSecondaryColor(request.getSecondaryColor());
        command.setFontFamily(request.getFontFamily());
        if (request.getTheme() != null) {
            command.setTheme(ReviseBrandingCommand.ThemeEnum.fromValue(request.getTheme()));
        }
        // After command, fetch updated projection from the query SDK
        return brandingDistributorApi.reviseBranding(distributorId, brandingId, command, UUID.randomUUID().toString())
                .flatMap(result -> getBranding(distributorId, brandingId))
                .doOnNext(b -> log.info("Updated branding: distributorId={}, brandingId={}",
                        distributorId, brandingId));
    }

    @Override
    public Mono<Void> deleteBranding(UUID distributorId, UUID brandingId) {
        log.info("Deleting branding {} for distributor: {}", brandingId, distributorId);
        return distributorQueryApi.deleteDistributorBranding(distributorId, brandingId, UUID.randomUUID().toString())
                .doOnSuccess(v -> log.info("Deleted branding: distributorId={}, brandingId={}",
                        distributorId, brandingId));
    }

    @Override
    public Mono<BrandingDTO> setDefaultBranding(UUID distributorId, UUID brandingId) {
        log.info("Setting default branding {} for distributor: {}", brandingId, distributorId);
        // Write command via domain SDK, then read updated projection
        return brandingDistributorApi.setDefaultBranding(distributorId, brandingId,
                        new SetDefaultBrandingCommand(), UUID.randomUUID().toString())
                .flatMap(result -> getBranding(distributorId, brandingId))
                .doOnNext(b -> log.info("Set default branding: distributorId={}, brandingId={}",
                        distributorId, brandingId));
    }

    private BrandingDTO toDTO(DistributorBrandingDTO sdkDto) {
        return BrandingDTO.builder()
                .id(sdkDto.getId())
                .distributorId(sdkDto.getDistributorId())
                .logoUrl(sdkDto.getLogoUrl())
                .primaryColor(sdkDto.getPrimaryColor())
                .secondaryColor(sdkDto.getSecondaryColor())
                .fontFamily(sdkDto.getFontFamily())
                .theme(sdkDto.getTheme() != null ? sdkDto.getTheme().getValue() : null)
                .isDefault(sdkDto.getIsDefault())
                .build();
    }
}
