package com.firefly.experience.distributor.core.branding;

import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BrandingService {

    Mono<UUID> reviseBranding(UUID distributorId, UUID brandingId, UpdateBrandingRequest request);

    Mono<UUID> setDefaultBranding(UUID distributorId, UUID brandingId);
}
