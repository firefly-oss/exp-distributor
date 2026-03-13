package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.terms.TermsAndConditionsService;
import com.firefly.experience.distributor.interfaces.dtos.CreateTermsRequest;
import com.firefly.experience.distributor.interfaces.dtos.TermsDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTermsRequest;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = TermsAndConditionsController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class TermsAndConditionsControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private TermsAndConditionsService termsAndConditionsService;

    // Firefly GlobalExceptionHandler is component-scanned into the slice; mock its required deps.
    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH =
            "/api/v1/experience/distributors/{distributorId}/terms-and-conditions";

    private TermsDTO buildTermsDTO(UUID distributorId, UUID tcId) {
        return TermsDTO.builder()
                .id(tcId)
                .distributorId(distributorId)
                .title("Standard T&C")
                .content("Terms content here")
                .version("1.0")
                .status("DRAFT")
                .effectiveDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .isActive(true)
                .build();
    }

    // ── GET / — listTerms ─────────────────────────────────────────────────────

    @Test
    void GET_listTerms_shouldReturn200WithList() {
        UUID distributorId = UUID.randomUUID();
        TermsDTO terms = buildTermsDTO(distributorId, UUID.randomUUID());

        when(termsAndConditionsService.listTerms(distributorId))
                .thenReturn(Flux.just(terms));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TermsDTO.class)
                .hasSize(1);
    }

    @Test
    void GET_listTerms_shouldReturnEmptyListWhenNoTerms() {
        UUID distributorId = UUID.randomUUID();

        when(termsAndConditionsService.listTerms(distributorId))
                .thenReturn(Flux.empty());

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TermsDTO.class)
                .hasSize(0);
    }

    // ── GET /active — getActiveTerms ──────────────────────────────────────────

    @Test
    void GET_activeTerms_shouldReturn200WithActiveList() {
        UUID distributorId = UUID.randomUUID();
        TermsDTO active = buildTermsDTO(distributorId, UUID.randomUUID());
        active.setStatus("ACTIVE");

        when(termsAndConditionsService.getActiveTerms(distributorId))
                .thenReturn(Flux.just(active));

        webClient.get()
                .uri(BASE_PATH + "/active", distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TermsDTO.class)
                .hasSize(1);
    }

    // ── GET /latest — getLatestTerms ──────────────────────────────────────────

    @Test
    void GET_latestTerms_shouldReturn200WithLatestDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        TermsDTO latest = buildTermsDTO(distributorId, tcId);

        when(termsAndConditionsService.getLatestTerms(distributorId))
                .thenReturn(Mono.just(latest));

        webClient.get()
                .uri(BASE_PATH + "/latest", distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TermsDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(tcId));
    }

    // ── POST / — createTerms ──────────────────────────────────────────────────

    @Test
    void POST_createTerms_shouldReturn201WithCreatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        TermsDTO created = buildTermsDTO(distributorId, tcId);

        when(termsAndConditionsService.createTerms(eq(distributorId), any(CreateTermsRequest.class)))
                .thenReturn(Mono.just(created));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "title": "Standard T&C",
                          "content": "Terms content here",
                          "version": "1.0",
                          "effectiveDate": "2026-01-01T00:00:00"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TermsDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                });

        verify(termsAndConditionsService).createTerms(eq(distributorId), any(CreateTermsRequest.class));
    }

    // ── GET /{tcId} — getTermsDetail ──────────────────────────────────────────

    @Test
    void GET_termsDetail_shouldReturn200WithDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        TermsDTO detail = buildTermsDTO(distributorId, tcId);

        when(termsAndConditionsService.getTermsDetail(distributorId, tcId))
                .thenReturn(Mono.just(detail));

        webClient.get()
                .uri(BASE_PATH + "/{tcId}", distributorId, tcId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TermsDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(tcId));
    }

    // ── PUT /{tcId} — updateTerms ─────────────────────────────────────────────

    @Test
    void PUT_updateTerms_shouldReturn200WithUpdatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        TermsDTO updated = buildTermsDTO(distributorId, tcId);
        updated.setVersion("2.0");

        when(termsAndConditionsService.updateTerms(eq(distributorId), eq(tcId), any(UpdateTermsRequest.class)))
                .thenReturn(Mono.just(updated));

        webClient.put()
                .uri(BASE_PATH + "/{tcId}", distributorId, tcId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "title": "Updated T&C",
                          "content": "Updated content",
                          "version": "2.0"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TermsDTO.class)
                .value(dto -> assertThat(dto.getVersion()).isEqualTo("2.0"));

        verify(termsAndConditionsService).updateTerms(eq(distributorId), eq(tcId), any(UpdateTermsRequest.class));
    }

    // ── DELETE /{tcId} — deleteTerms ─────────────────────────────────────────

    @Test
    void DELETE_deleteTerms_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();

        when(termsAndConditionsService.deleteTerms(distributorId, tcId))
                .thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{tcId}", distributorId, tcId)
                .exchange()
                .expectStatus().isNoContent();

        verify(termsAndConditionsService).deleteTerms(distributorId, tcId);
    }

    // ── PATCH /{tcId}/sign — signTerms ────────────────────────────────────────

    @Test
    void PATCH_signTerms_shouldReturn200WithSignedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        TermsDTO signed = buildTermsDTO(distributorId, tcId);
        signed.setStatus("SIGNED");
        signed.setSignedDate(LocalDateTime.of(2026, 3, 11, 10, 0));

        when(termsAndConditionsService.signTerms(distributorId, tcId))
                .thenReturn(Mono.just(signed));

        webClient.patch()
                .uri(BASE_PATH + "/{tcId}/sign", distributorId, tcId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TermsDTO.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getStatus()).isEqualTo("SIGNED");
                    assertThat(dto.getSignedDate()).isNotNull();
                });

        verify(termsAndConditionsService).signTerms(distributorId, tcId);
    }

    // ── PATCH /{tcId}/activate — activateTerms ────────────────────────────────

    @Test
    void PATCH_activateTerms_shouldReturn200WithActivatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        TermsDTO activated = buildTermsDTO(distributorId, tcId);
        activated.setStatus("ACTIVE");

        when(termsAndConditionsService.activateTerms(distributorId, tcId))
                .thenReturn(Mono.just(activated));

        webClient.patch()
                .uri(BASE_PATH + "/{tcId}/activate", distributorId, tcId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TermsDTO.class)
                .value(dto -> assertThat(dto.getStatus()).isEqualTo("ACTIVE"));

        verify(termsAndConditionsService).activateTerms(distributorId, tcId);
    }

    // ── PATCH /{tcId}/deactivate — deactivateTerms ────────────────────────────

    @Test
    void PATCH_deactivateTerms_shouldReturn200WithDeactivatedDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        TermsDTO deactivated = buildTermsDTO(distributorId, tcId);
        deactivated.setIsActive(false);

        when(termsAndConditionsService.deactivateTerms(distributorId, tcId))
                .thenReturn(Mono.just(deactivated));

        webClient.patch()
                .uri(BASE_PATH + "/{tcId}/deactivate", distributorId, tcId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TermsDTO.class)
                .value(dto -> assertThat(dto.getIsActive()).isFalse());

        verify(termsAndConditionsService).deactivateTerms(distributorId, tcId);
    }

    // ── GET /has-active-signed — hasActiveSignedTerms ─────────────────────────

    @Test
    void GET_hasActiveSignedTerms_shouldReturn200WithTrue() {
        UUID distributorId = UUID.randomUUID();

        when(termsAndConditionsService.hasActiveSignedTerms(distributorId))
                .thenReturn(Mono.just(true));

        webClient.get()
                .uri(BASE_PATH + "/has-active-signed", distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void GET_hasActiveSignedTerms_shouldReturn200WithFalse() {
        UUID distributorId = UUID.randomUUID();

        when(termsAndConditionsService.hasActiveSignedTerms(distributorId))
                .thenReturn(Mono.just(false));

        webClient.get()
                .uri(BASE_PATH + "/has-active-signed", distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(false);
    }
}
