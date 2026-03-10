package com.firefly.experience.distributor.core.profile;

import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.model.RegisterDistributorCommand;
import com.firefly.domain.distributor.branding.sdk.model.RegisterDistributorInfoCommand;
import com.firefly.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributorProfileServiceImpl implements DistributorProfileService {

    @Qualifier("brandingDistributorApi")
    private final DistributorApi brandingDistributorApi;

    @Override
    public Mono<UUID> registerDistributor(RegisterDistributorRequest request) {
        log.info("Registering distributor with name: {}", request.getName());

        RegisterDistributorInfoCommand infoCommand = new RegisterDistributorInfoCommand();
        infoCommand.setName(request.getName());
        infoCommand.setDisplayName(request.getDisplayName());
        infoCommand.setEmail(request.getEmail());
        infoCommand.setPhoneNumber(request.getPhoneNumber());
        infoCommand.setTaxId(request.getTaxId());

        RegisterDistributorCommand sdkCommand = new RegisterDistributorCommand();
        sdkCommand.setDistributorInfo(infoCommand);

        return brandingDistributorApi.onboardDistributor(sdkCommand)
                .map(result -> (UUID) result);
    }
}
