package com.firefly.experience.distributor.core.config;

import com.firefly.experience.distributor.interfaces.dtos.ConfigurationDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConfigurationService {

    Flux<ConfigurationDTO> listConfigurations(UUID distributorId);

    Mono<ConfigurationDTO> createConfiguration(UUID distributorId, CreateConfigurationRequest request);

    Mono<ConfigurationDTO> updateConfiguration(UUID distributorId, UUID configId, UpdateConfigurationRequest request);

    Mono<Void> deleteConfiguration(UUID distributorId, UUID configId);
}
