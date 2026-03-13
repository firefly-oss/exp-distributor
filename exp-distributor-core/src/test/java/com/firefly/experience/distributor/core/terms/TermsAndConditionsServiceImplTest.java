package com.firefly.experience.distributor.core.terms;

import com.firefly.domain.distributor.branding.sdk.api.TermsAndConditionsApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateTermsAndConditionsCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorTermsAndConditionsDTO;
import com.firefly.experience.distributor.core.mappers.TermsMapper;
import com.firefly.experience.distributor.interfaces.dtos.CreateTermsRequest;
import com.firefly.experience.distributor.interfaces.dtos.TermsDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TermsAndConditionsServiceImplTest {

    @Mock
    private TermsAndConditionsApi termsAndConditionsApi;

    @Mock
    private TermsMapper termsMapper;

    @InjectMocks
    private TermsAndConditionsServiceImpl service;

    // ── helpers ───────────────────────────────────────────────────────────────

    private DistributorTermsAndConditionsDTO buildSdkDto(UUID id, UUID distributorId) {
        DistributorTermsAndConditionsDTO dto = new DistributorTermsAndConditionsDTO();
        dto.setId(id);
        dto.setDistributorId(distributorId);
        dto.setTitle("Test T&C");
        dto.setVersion("1.0");
        dto.setStatus("DRAFT");
        dto.setIsActive(true);
        return dto;
    }

    private TermsDTO buildTermsDTO(UUID id, UUID distributorId) {
        return TermsDTO.builder()
                .id(id)
                .distributorId(distributorId)
                .title("Test T&C")
                .version("1.0")
                .status("DRAFT")
                .isActive(true)
                .build();
    }

    // ── listTerms ─────────────────────────────────────────────────────────────

    @Test
    void listTerms_shouldDelegateToApiAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(tcId, distributorId);
        TermsDTO expectedDto = buildTermsDTO(tcId, distributorId);

        when(termsAndConditionsApi.listTermsAndConditions(distributorId)).thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.listTerms(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getTitle()).isEqualTo("Test T&C");
                })
                .verifyComplete();

        verify(termsAndConditionsApi).listTermsAndConditions(distributorId);
    }

    // ── getActiveTerms ────────────────────────────────────────────────────────

    @Test
    void getActiveTerms_shouldDelegateToApiAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(tcId, distributorId);
        sdkDto.setStatus("ACTIVE");
        TermsDTO expectedDto = buildTermsDTO(tcId, distributorId);
        expectedDto.setStatus("ACTIVE");

        when(termsAndConditionsApi.getActiveTermsAndConditions(distributorId)).thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.getActiveTerms(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getStatus()).isEqualTo("ACTIVE");
                })
                .verifyComplete();

        verify(termsAndConditionsApi).getActiveTermsAndConditions(distributorId);
    }

    // ── getLatestTerms ────────────────────────────────────────────────────────

    @Test
    void getLatestTerms_shouldDelegateToApiAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(tcId, distributorId);
        TermsDTO expectedDto = buildTermsDTO(tcId, distributorId);

        when(termsAndConditionsApi.getLatestTermsAndConditions(distributorId)).thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.getLatestTerms(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(termsAndConditionsApi).getLatestTermsAndConditions(distributorId);
    }

    // ── createTerms ───────────────────────────────────────────────────────────

    @Test
    void createTerms_shouldCreateThenFetchDetailAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID newTcId = UUID.randomUUID();
        CreateTermsRequest request = CreateTermsRequest.builder()
                .title("New T&C")
                .content("Content here")
                .version("1.0")
                .effectiveDate(LocalDateTime.now())
                .build();
        CreateTermsAndConditionsCommand command = new CreateTermsAndConditionsCommand();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(newTcId, distributorId);
        TermsDTO expectedDto = buildTermsDTO(newTcId, distributorId);

        when(termsMapper.toCreateCommand(request)).thenReturn(command);
        when(termsAndConditionsApi.createTermsAndConditions(distributorId, command))
                .thenReturn(Mono.just(newTcId));
        when(termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, newTcId))
                .thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.createTerms(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(newTcId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(termsAndConditionsApi).createTermsAndConditions(distributorId, command);
        verify(termsAndConditionsApi).getTermsAndConditionsDetail(distributorId, newTcId);
    }

    // ── getTermsDetail ────────────────────────────────────────────────────────

    @Test
    void getTermsDetail_shouldDelegateToApiAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(tcId, distributorId);
        TermsDTO expectedDto = buildTermsDTO(tcId, distributorId);

        when(termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, tcId))
                .thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.getTermsDetail(distributorId, tcId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(termsAndConditionsApi).getTermsAndConditionsDetail(distributorId, tcId);
    }

    // ── updateTerms ───────────────────────────────────────────────────────────

    @Test
    void updateTerms_shouldDeleteOldThenCreateNewAndFetchDetail() {
        UUID distributorId = UUID.randomUUID();
        UUID oldTcId = UUID.randomUUID();
        UUID newTcId = UUID.randomUUID();
        UpdateTermsRequest request = UpdateTermsRequest.builder()
                .title("Updated T&C")
                .content("Updated content")
                .version("2.0")
                .build();
        CreateTermsAndConditionsCommand command = new CreateTermsAndConditionsCommand();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(newTcId, distributorId);
        sdkDto.setVersion("2.0");
        TermsDTO expectedDto = buildTermsDTO(newTcId, distributorId);
        expectedDto.setVersion("2.0");

        when(termsMapper.toUpdateCommand(request)).thenReturn(command);
        when(termsAndConditionsApi.deleteTermsAndConditions(distributorId, oldTcId))
                .thenReturn(Mono.empty());
        when(termsAndConditionsApi.createTermsAndConditions(distributorId, command))
                .thenReturn(Mono.just(newTcId));
        when(termsAndConditionsApi.getTermsAndConditionsDetail(distributorId, newTcId))
                .thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.updateTerms(distributorId, oldTcId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(newTcId);
                    assertThat(dto.getVersion()).isEqualTo("2.0");
                })
                .verifyComplete();

        verify(termsAndConditionsApi).deleteTermsAndConditions(distributorId, oldTcId);
        verify(termsAndConditionsApi).createTermsAndConditions(distributorId, command);
        verify(termsAndConditionsApi).getTermsAndConditionsDetail(distributorId, newTcId);
    }

    // ── deleteTerms ───────────────────────────────────────────────────────────

    @Test
    void deleteTerms_shouldDelegateToApiAndCompleteEmpty() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();

        when(termsAndConditionsApi.deleteTermsAndConditions(distributorId, tcId))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.deleteTerms(distributorId, tcId))
                .verifyComplete();

        verify(termsAndConditionsApi).deleteTermsAndConditions(distributorId, tcId);
    }

    // ── signTerms ─────────────────────────────────────────────────────────────

    @Test
    void signTerms_shouldCallSignApiWithGeneratedActorIdAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(tcId, distributorId);
        sdkDto.setStatus("SIGNED");
        TermsDTO expectedDto = buildTermsDTO(tcId, distributorId);
        expectedDto.setStatus("SIGNED");
        expectedDto.setSignedDate(LocalDateTime.now());

        when(termsAndConditionsApi.signTermsAndConditions(eq(distributorId), eq(tcId), any(UUID.class)))
                .thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.signTerms(distributorId, tcId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getStatus()).isEqualTo("SIGNED");
                    assertThat(dto.getSignedDate()).isNotNull();
                })
                .verifyComplete();

        verify(termsAndConditionsApi).signTermsAndConditions(eq(distributorId), eq(tcId), any(UUID.class));
    }

    // ── activateTerms ─────────────────────────────────────────────────────────

    @Test
    void activateTerms_shouldCallActivateApiAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(tcId, distributorId);
        sdkDto.setStatus("ACTIVE");
        TermsDTO expectedDto = buildTermsDTO(tcId, distributorId);
        expectedDto.setStatus("ACTIVE");

        when(termsAndConditionsApi.activateTermsAndConditions(eq(distributorId), eq(tcId), any(UUID.class)))
                .thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.activateTerms(distributorId, tcId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getStatus()).isEqualTo("ACTIVE");
                })
                .verifyComplete();

        verify(termsAndConditionsApi).activateTermsAndConditions(eq(distributorId), eq(tcId), any(UUID.class));
    }

    // ── deactivateTerms ───────────────────────────────────────────────────────

    @Test
    void deactivateTerms_shouldCallDeactivateApiAndMapToDto() {
        UUID distributorId = UUID.randomUUID();
        UUID tcId = UUID.randomUUID();
        DistributorTermsAndConditionsDTO sdkDto = buildSdkDto(tcId, distributorId);
        sdkDto.setIsActive(false);
        TermsDTO expectedDto = buildTermsDTO(tcId, distributorId);
        expectedDto.setIsActive(false);

        when(termsAndConditionsApi.deactivateTermsAndConditions(eq(distributorId), eq(tcId), any(UUID.class)))
                .thenReturn(Mono.just(sdkDto));
        when(termsMapper.toDto(sdkDto)).thenReturn(expectedDto);

        StepVerifier.create(service.deactivateTerms(distributorId, tcId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(tcId);
                    assertThat(dto.getIsActive()).isFalse();
                })
                .verifyComplete();

        verify(termsAndConditionsApi).deactivateTermsAndConditions(eq(distributorId), eq(tcId), any(UUID.class));
    }

    // ── hasActiveSignedTerms ──────────────────────────────────────────────────

    @Test
    void hasActiveSignedTerms_shouldReturnTrueWhenApiReturnsTrue() {
        UUID distributorId = UUID.randomUUID();

        when(termsAndConditionsApi.hasActiveSignedTerms(distributorId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(service.hasActiveSignedTerms(distributorId))
                .assertNext(result -> assertThat(result).isTrue())
                .verifyComplete();

        verify(termsAndConditionsApi).hasActiveSignedTerms(distributorId);
    }

    @Test
    void hasActiveSignedTerms_shouldReturnFalseWhenApiReturnsFalse() {
        UUID distributorId = UUID.randomUUID();

        when(termsAndConditionsApi.hasActiveSignedTerms(distributorId))
                .thenReturn(Mono.just(false));

        StepVerifier.create(service.hasActiveSignedTerms(distributorId))
                .assertNext(result -> assertThat(result).isFalse())
                .verifyComplete();
    }
}
