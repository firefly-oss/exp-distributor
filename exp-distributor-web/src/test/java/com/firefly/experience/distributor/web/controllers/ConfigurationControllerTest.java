package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.config.ConfigurationService;
import com.firefly.experience.distributor.interfaces.dtos.ConfigurationDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
import org.fireflyframework.web.error.config.ErrorHandlingProperties;
import org.fireflyframework.web.error.converter.ExceptionConverterService;
import org.fireflyframework.web.error.service.ErrorResponseNegotiator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ConfigurationController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class ConfigurationControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ConfigurationService configurationService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/configurations";

    private ConfigurationDTO buildConfigurationDTO(UUID distributorId, UUID configId) {
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
    void GET_listConfigurations_shouldReturn200WithListOfConfigurations() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();
        ConfigurationDTO dto = buildConfigurationDTO(distributorId, configId);

        when(configurationService.listConfigurations(distributorId)).thenReturn(Flux.just(dto));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ConfigurationDTO.class)
                .value(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getId()).isEqualTo(configId);
                    assertThat(list.get(0).getConfigKey()).isEqualTo("feature.enabled");
                });

        verify(configurationService).listConfigurations(distributorId);
    }

    @Test
    void POST_createConfiguration_shouldReturn201WithConfigurationDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();
        ConfigurationDTO dto = buildConfigurationDTO(distributorId, configId);

        when(configurationService.createConfiguration(eq(distributorId), any(CreateConfigurationRequest.class)))
                .thenReturn(Mono.just(dto));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"configKey":"feature.enabled","configValue":"true","category":"FEATURE_FLAGS"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ConfigurationDTO.class)
                .value(result -> {
                    assertThat(result.getId()).isEqualTo(configId);
                    assertThat(result.getConfigKey()).isEqualTo("feature.enabled");
                });
    }

    @Test
    void PUT_updateConfiguration_shouldReturn200WithUpdatedConfigurationDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();
        ConfigurationDTO dto = buildConfigurationDTO(distributorId, configId);

        when(configurationService.updateConfiguration(eq(distributorId), eq(configId), any(UpdateConfigurationRequest.class)))
                .thenReturn(Mono.just(dto));

        webClient.put()
                .uri(BASE_PATH + "/{configId}", distributorId, configId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"configKey":"feature.enabled","configValue":"false"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ConfigurationDTO.class)
                .value(result -> assertThat(result.getId()).isEqualTo(configId));

        verify(configurationService).updateConfiguration(eq(distributorId), eq(configId), any(UpdateConfigurationRequest.class));
    }

    @Test
    void DELETE_deleteConfiguration_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID configId = UUID.randomUUID();

        when(configurationService.deleteConfiguration(distributorId, configId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{configId}", distributorId, configId)
                .exchange()
                .expectStatus().isNoContent();

        verify(configurationService).deleteConfiguration(distributorId, configId);
    }
}
