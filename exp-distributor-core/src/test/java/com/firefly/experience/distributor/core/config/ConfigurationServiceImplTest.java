package com.firefly.experience.distributor.core.config;

import com.firefly.domain.distributor.branding.sdk.api.ConfigurationApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateConfigurationCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorConfigurationDTO;
import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.domain.distributor.branding.sdk.model.UpdateConfigurationCommand;
import com.firefly.experience.distributor.core.mappers.ConfigurationMapper;
import com.firefly.experience.distributor.interfaces.dtos.ConfigurationDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

    @Mock
    private ConfigurationApi configurationApi;

    @Mock
    private ConfigurationMapper configurationMapper;

    @InjectMocks
    private ConfigurationServiceImpl service;

    private DistributorConfigurationDTO buildSdkConfig(UUID distributorId, UUID configId) {
        DistributorConfigurationDTO dto = new DistributorConfigurationDTO();
        dto.setId(configId);
        dto.setDistributorId(distributorId);
        dto.setConfigKey("feature.enabled");
        dto.setConfigValue("true");
        dto.setCategory("FEATURE_FLAGS");
        dto.setIsActive(true);
        return dto;
    }

    private ConfigurationDTO buildConfigDTO(UUID distributorId, UUID configId) {
        return ConfigurationDTO.builder()
                .id(configId)
                .distributorId(distributorId)
                .configKey("feature.enabled")
                .configValue("true")
                .category("FEATURE_FLAGS")
                .isActive(true)
                .build();
    }

    @Test
    void listConfigurations_shouldFlatMapPaginationContentAndMapEachItem() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();
        DistributorConfigurationDTO sdkDto = buildSdkConfig(distributorId, configId);
        ConfigurationDTO expected = buildConfigDTO(distributorId, configId);

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setContent(List.of(sdkDto));

        when(configurationApi.listConfigurations(distributorId)).thenReturn(Mono.just(paginationResponse));
        when(configurationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.listConfigurations(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(configId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getConfigKey()).isEqualTo("feature.enabled");
                    assertThat(dto.getIsActive()).isTrue();
                })
                .verifyComplete();

        verify(configurationApi).listConfigurations(distributorId);
        verify(configurationMapper).toDto(sdkDto);
    }

    @Test
    void listConfigurations_whenContentIsEmpty_shouldCompleteWithNoElements() {
        UUID distributorId = UUID.randomUUID();
        PaginationResponse emptyResponse = new PaginationResponse();
        emptyResponse.setContent(List.of());

        when(configurationApi.listConfigurations(distributorId)).thenReturn(Mono.just(emptyResponse));

        StepVerifier.create(service.listConfigurations(distributorId))
                .verifyComplete();

        verify(configurationApi).listConfigurations(distributorId);
    }

    @Test
    void createConfiguration_shouldMapRequestFetchAndReturnDto() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();
        CreateConfigurationRequest request = CreateConfigurationRequest.builder()
                .configKey("feature.enabled")
                .configValue("true")
                .category("FEATURE_FLAGS")
                .build();
        CreateConfigurationCommand command = new CreateConfigurationCommand();
        DistributorConfigurationDTO sdkDto = buildSdkConfig(distributorId, configId);
        ConfigurationDTO expected = buildConfigDTO(distributorId, configId);

        when(configurationMapper.toCreateCommand(request)).thenReturn(command);
        when(configurationApi.createConfiguration(distributorId, command)).thenReturn(Mono.just(configId));
        when(configurationApi.getConfiguration(distributorId, configId)).thenReturn(Mono.just(sdkDto));
        when(configurationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.createConfiguration(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(configId);
                    assertThat(dto.getConfigKey()).isEqualTo("feature.enabled");
                })
                .verifyComplete();

        verify(configurationMapper).toCreateCommand(request);
        verify(configurationApi).createConfiguration(distributorId, command);
        verify(configurationApi).getConfiguration(distributorId, configId);
        verify(configurationMapper).toDto(sdkDto);
    }

    @Test
    void updateConfiguration_shouldMapRequestFetchAndReturnDto() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();
        UpdateConfigurationRequest request = UpdateConfigurationRequest.builder()
                .configKey("feature.enabled")
                .configValue("false")
                .build();
        UpdateConfigurationCommand command = new UpdateConfigurationCommand();
        DistributorConfigurationDTO sdkDto = buildSdkConfig(distributorId, configId);
        ConfigurationDTO expected = buildConfigDTO(distributorId, configId);

        when(configurationMapper.toUpdateCommand(request)).thenReturn(command);
        when(configurationApi.updateConfiguration(distributorId, configId, command)).thenReturn(Mono.just(configId));
        when(configurationApi.getConfiguration(distributorId, configId)).thenReturn(Mono.just(sdkDto));
        when(configurationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.updateConfiguration(distributorId, configId, request))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(configId))
                .verifyComplete();

        verify(configurationMapper).toUpdateCommand(request);
        verify(configurationApi).updateConfiguration(distributorId, configId, command);
        verify(configurationApi).getConfiguration(distributorId, configId);
    }

    @Test
    void deleteConfiguration_shouldDelegateToApiAndComplete() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();

        when(configurationApi.deleteConfiguration(distributorId, configId)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteConfiguration(distributorId, configId))
                .verifyComplete();

        verify(configurationApi).deleteConfiguration(distributorId, configId);
    }
}
