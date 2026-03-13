package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.branding.BrandingService;
import com.firefly.experience.distributor.interfaces.dtos.BrandingDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateBrandingRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
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

@WebFluxTest(controllers = BrandingController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class BrandingControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private BrandingService brandingService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/brandings";

    private BrandingDTO buildBrandingDTO(UUID distributorId, UUID brandingId) {
        return BrandingDTO.builder()
                .id(brandingId)
                .distributorId(distributorId)
                .logoUrl("https://example.com/logo.png")
                .primaryColor("#FF0000")
                .isDefault(false)
                .build();
    }

    @Test
    void GET_listBrandings_shouldReturn200WithList() {
        UUID distributorId = UUID.randomUUID();
        BrandingDTO branding = buildBrandingDTO(distributorId, UUID.randomUUID());
        when(brandingService.listBrandings(distributorId)).thenReturn(Flux.just(branding));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BrandingDTO.class)
                .hasSize(1);
    }

    @Test
    void POST_createBranding_shouldReturn201WithCreatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        BrandingDTO created = buildBrandingDTO(distributorId, brandingId);

        when(brandingService.createBranding(eq(distributorId), any(CreateBrandingRequest.class)))
                .thenReturn(Mono.just(created));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"logoUrl":"https://example.com/logo.png","primaryColor":"#FF0000"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BrandingDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(brandingId));
    }

    @Test
    void GET_getBranding_shouldReturn200WithDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        BrandingDTO branding = buildBrandingDTO(distributorId, brandingId);

        when(brandingService.getBranding(distributorId, brandingId)).thenReturn(Mono.just(branding));

        webClient.get()
                .uri(BASE_PATH + "/{brandingId}", distributorId, brandingId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BrandingDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(brandingId));
    }

    @Test
    void PUT_updateBranding_shouldReturn200WithUpdatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        BrandingDTO updated = buildBrandingDTO(distributorId, brandingId);

        when(brandingService.updateBranding(eq(distributorId), eq(brandingId), any(UpdateBrandingRequest.class)))
                .thenReturn(Mono.just(updated));

        webClient.put()
                .uri(BASE_PATH + "/{brandingId}", distributorId, brandingId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"logoUrl":"https://example.com/new-logo.png"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BrandingDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(brandingId));

        verify(brandingService).updateBranding(eq(distributorId), eq(brandingId), any());
    }

    @Test
    void DELETE_deleteBranding_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        when(brandingService.deleteBranding(distributorId, brandingId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{brandingId}", distributorId, brandingId)
                .exchange()
                .expectStatus().isNoContent();

        verify(brandingService).deleteBranding(distributorId, brandingId);
    }

    @Test
    void PUT_setDefaultBranding_shouldReturn200WithUpdatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        BrandingDTO defaultBranding = buildBrandingDTO(distributorId, brandingId);
        defaultBranding.setIsDefault(true);

        when(brandingService.setDefaultBranding(distributorId, brandingId))
                .thenReturn(Mono.just(defaultBranding));

        webClient.put()
                .uri(BASE_PATH + "/{brandingId}/set-default", distributorId, brandingId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BrandingDTO.class)
                .value(dto -> assertThat(dto.getIsDefault()).isTrue());

        verify(brandingService).setDefaultBranding(distributorId, brandingId);
    }
}
