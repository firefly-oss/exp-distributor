package com.firefly.experience.distributor.core.branding;

import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.model.ReviseBrandingCommand;
import com.firefly.domain.distributor.branding.sdk.model.SetDefaultBrandingCommand;
import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandingServiceImpl implements BrandingService {

    @Qualifier("brandingDistributorApi")
    private final DistributorApi brandingDistributorApi;

    @Override
    public Mono<UUID> reviseBranding(UUID distributorId, UUID brandingId, UpdateBrandingRequest request) {
        log.info("Revising branding {} for distributor: {}", brandingId, distributorId);

        ReviseBrandingCommand command = new ReviseBrandingCommand();
        command.setLogoUrl(request.getLogoUrl());
        command.setPrimaryColor(request.getPrimaryColor());
        command.setSecondaryColor(request.getSecondaryColor());
        command.setFontFamily(request.getFontFamily());
        if (request.getTheme() != null) {
            command.setTheme(ReviseBrandingCommand.ThemeEnum.fromValue(request.getTheme()));
        }

        return brandingDistributorApi.reviseBranding(distributorId, brandingId, command)
                .map(result -> (UUID) result);
    }

    @Override
    public Mono<UUID> setDefaultBranding(UUID distributorId, UUID brandingId) {
        log.info("Setting default branding {} for distributor: {}", brandingId, distributorId);

        SetDefaultBrandingCommand command = new SetDefaultBrandingCommand();

        return brandingDistributorApi.setDefaultBranding(distributorId, brandingId, command)
                .map(result -> (UUID) result);
    }
}
