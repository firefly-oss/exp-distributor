package com.firefly.experience.distributor.core.config;

import com.firefly.domain.distributor.branding.sdk.api.ConfigurationApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateConfigurationCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorConfigurationDTO;
import com.firefly.domain.distributor.branding.sdk.model.UpdateConfigurationCommand;
import com.firefly.experience.distributor.interfaces.dtos.ConfigurationDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
import com.firefly.experience.distributor.core.mappers.ConfigurationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationApi configurationApi;
    private final ConfigurationMapper configurationMapper;

    @Override
    public Flux<ConfigurationDTO> listConfigurations(UUID distributorId) {
        log.info("Listing configurations for distributor: {}", distributorId);
        return configurationApi.listConfigurations(distributorId)
                .flatMapMany(paginationResponse -> Flux.fromIterable(paginationResponse.getContent()))
                .map(item -> {
                    DistributorConfigurationDTO sdk = mapObjectToConfigSdk(item);
                    return configurationMapper.toDto(sdk);
                });
    }

    @Override
    public Mono<ConfigurationDTO> createConfiguration(UUID distributorId, CreateConfigurationRequest request) {
        log.info("Creating configuration for distributor: {}", distributorId);
        CreateConfigurationCommand command = configurationMapper.toCreateCommand(request);
        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on createConfiguration; idempotency cannot be set at call-site.
        return configurationApi.createConfiguration(distributorId, command)
                .flatMap(configId -> configurationApi.getConfiguration(distributorId, configId))
                .map(configurationMapper::toDto);
    }

    @Override
    public Mono<ConfigurationDTO> updateConfiguration(UUID distributorId, UUID configId, UpdateConfigurationRequest request) {
        log.info("Updating configuration {} for distributor: {}", configId, distributorId);
        UpdateConfigurationCommand command = configurationMapper.toUpdateCommand(request);
        // ARCH-EXCEPTION: domain-distributor-branding-sdk generated client does not expose an
        // xIdempotencyKey parameter on updateConfiguration; idempotency cannot be set at call-site.
        return configurationApi.updateConfiguration(distributorId, configId, command)
                .flatMap(updatedId -> configurationApi.getConfiguration(distributorId, updatedId))
                .map(configurationMapper::toDto);
    }

    @Override
    public Mono<Void> deleteConfiguration(UUID distributorId, UUID configId) {
        log.info("Deleting configuration {} for distributor: {}", configId, distributorId);
        return configurationApi.deleteConfiguration(distributorId, configId);
    }

    @SuppressWarnings("unchecked")
    private DistributorConfigurationDTO mapObjectToConfigSdk(Object item) {
        if (item instanceof DistributorConfigurationDTO) {
            return (DistributorConfigurationDTO) item;
        }
        if (item instanceof java.util.Map) {
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) item;
            DistributorConfigurationDTO dto = new DistributorConfigurationDTO();
            dto.setId(mapUuid(map.get("id")));
            dto.setDistributorId(mapUuid(map.get("distributorId")));
            dto.setConfigKey(map.get("configKey") != null ? map.get("configKey").toString() : null);
            dto.setConfigValue(map.get("configValue") != null ? map.get("configValue").toString() : null);
            dto.setCategory(map.get("category") != null ? map.get("category").toString() : null);
            dto.setDescription(map.get("description") != null ? map.get("description").toString() : null);
            dto.setIsActive(map.get("isActive") != null ? (Boolean) map.get("isActive") : null);
            return dto;
        }
        throw new IllegalArgumentException("Cannot map object to DistributorConfigurationDTO");
    }

    private UUID mapUuid(Object value) {
        if (value == null) return null;
        if (value instanceof UUID) return (UUID) value;
        return UUID.fromString(value.toString());
    }
}
