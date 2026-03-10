package com.firefly.experience.distributor.core.profile;

import com.firefly.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DistributorProfileService {

    Mono<UUID> registerDistributor(RegisterDistributorRequest request);
}
