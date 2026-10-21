/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.experience.distributor.core.config;

import io.fireflysoftware.domain.distributor.branding.sdk.api.ConfigurationApi;
import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateConfigurationCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorConfigurationDTO;
import io.fireflysoftware.domain.distributor.branding.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateConfigurationCommand;
import io.fireflysoftware.experience.distributor.core.mappers.ConfigurationMapper;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ConfigurationDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        when(configurationApi.listConfigurations(eq(distributorId), any())).thenReturn(Mono.just(paginationResponse));
        when(configurationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.listConfigurations(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(configId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getConfigKey()).isEqualTo("feature.enabled");
                    assertThat(dto.getIsActive()).isTrue();
                })
                .verifyComplete();

        verify(configurationApi).listConfigurations(eq(distributorId), any());
        verify(configurationMapper).toDto(sdkDto);
    }

    @Test
    void listConfigurations_whenContentIsEmpty_shouldCompleteWithNoElements() {
        UUID distributorId = UUID.randomUUID();
        PaginationResponse emptyResponse = new PaginationResponse();
        emptyResponse.setContent(List.of());

        when(configurationApi.listConfigurations(eq(distributorId), any())).thenReturn(Mono.just(emptyResponse));

        StepVerifier.create(service.listConfigurations(distributorId))
                .verifyComplete();

        verify(configurationApi).listConfigurations(eq(distributorId), any());
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
        when(configurationApi.createConfiguration(eq(distributorId), eq(command), any())).thenReturn(Mono.just(configId));
        when(configurationApi.getConfiguration(eq(distributorId), eq(configId), any())).thenReturn(Mono.just(sdkDto));
        when(configurationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.createConfiguration(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(configId);
                    assertThat(dto.getConfigKey()).isEqualTo("feature.enabled");
                })
                .verifyComplete();

        verify(configurationMapper).toCreateCommand(request);
        verify(configurationApi).createConfiguration(eq(distributorId), eq(command), any());
        verify(configurationApi).getConfiguration(eq(distributorId), eq(configId), any());
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
        when(configurationApi.updateConfiguration(eq(distributorId), eq(configId), eq(command), any())).thenReturn(Mono.just(configId));
        when(configurationApi.getConfiguration(eq(distributorId), eq(configId), any())).thenReturn(Mono.just(sdkDto));
        when(configurationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.updateConfiguration(distributorId, configId, request))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(configId))
                .verifyComplete();

        verify(configurationMapper).toUpdateCommand(request);
        verify(configurationApi).updateConfiguration(eq(distributorId), eq(configId), eq(command), any());
        verify(configurationApi).getConfiguration(eq(distributorId), eq(configId), any());
    }

    @Test
    void deleteConfiguration_shouldDelegateToApiAndComplete() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();

        when(configurationApi.deleteConfiguration(eq(distributorId), eq(configId), any())).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteConfiguration(distributorId, configId))
                .verifyComplete();

        verify(configurationApi).deleteConfiguration(eq(distributorId), eq(configId), any());
    }
}
