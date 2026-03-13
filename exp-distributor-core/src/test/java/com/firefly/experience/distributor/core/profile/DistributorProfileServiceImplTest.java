package com.firefly.experience.distributor.core.profile;

import com.firefly.core.distributor.sdk.model.DistributorDTO;
import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.model.RegisterDistributorCommand;
import com.firefly.experience.distributor.core.branding.BrandingService;
import com.firefly.experience.distributor.core.terms.TermsAndConditionsService;
import com.firefly.experience.distributor.interfaces.dtos.BrandingDTO;
import com.firefly.experience.distributor.interfaces.dtos.DistributorDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterDistributorRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateDistributorRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistributorProfileServiceImplTest {

    @Mock
    private DistributorApi brandingDistributorApi;

    @Mock
    private com.firefly.core.distributor.sdk.api.DistributorApi coreDistributorApi;

    @Mock
    private TermsAndConditionsService termsAndConditionsService;

    @Mock
    private BrandingService brandingService;

    @InjectMocks
    private DistributorProfileServiceImpl service;

    @Test
    void registerDistributor_shouldCallOnboardAndReturnDetailDTO() {
        UUID distributorId = UUID.randomUUID();
        RegisterDistributorRequest request = RegisterDistributorRequest.builder()
                .name("Acme Corp")
                .displayName("Acme")
                .email("info@acme.com")
                .phoneNumber("+1234567890")
                .taxId("US-TAX-001")
                .build();

        when(brandingDistributorApi.onboardDistributor(any(RegisterDistributorCommand.class)))
                .thenReturn(Mono.just(distributorId));

        StepVerifier.create(service.registerDistributor(request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(distributorId);
                    assertThat(dto.getName()).isEqualTo("Acme Corp");
                    assertThat(dto.getStatus()).isEqualTo("ACTIVE");
                    assertThat(dto.getHasActiveSignedTerms()).isFalse();
                })
                .verifyComplete();

        verify(brandingDistributorApi).onboardDistributor(any(RegisterDistributorCommand.class));
    }

    @Test
    void getDistributorDetail_shouldComposeFanOutFromMultipleSources() {
        UUID distributorId = UUID.randomUUID();

        DistributorDTO coreProfile = new DistributorDTO();
        coreProfile.setName("Acme Corp");
        coreProfile.setDisplayName("Acme");
        coreProfile.setEmail("info@acme.com");
        coreProfile.setIsActive(true);

        BrandingDTO defaultBranding = BrandingDTO.builder()
                .id(UUID.randomUUID())
                .distributorId(distributorId)
                .logoUrl("https://example.com/logo.png")
                .isDefault(true)
                .build();

        when(coreDistributorApi.getDistributorById(distributorId)).thenReturn(Mono.just(coreProfile));
        when(termsAndConditionsService.hasActiveSignedTerms(distributorId)).thenReturn(Mono.just(true));
        when(brandingService.listBrandings(distributorId)).thenReturn(Flux.just(defaultBranding));

        StepVerifier.create(service.getDistributorDetail(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(distributorId);
                    assertThat(dto.getName()).isEqualTo("Acme Corp");
                    assertThat(dto.getStatus()).isEqualTo("ACTIVE");
                    assertThat(dto.getHasActiveSignedTerms()).isTrue();
                    assertThat(dto.getActiveBranding()).isNotNull();
                    assertThat(dto.getActiveBranding().getLogoUrl()).isEqualTo("https://example.com/logo.png");
                })
                .verifyComplete();
    }

    @Test
    void getDistributorDetail_shouldReturnEmptyBrandingWhenNoneIsDefault() {
        UUID distributorId = UUID.randomUUID();

        DistributorDTO coreProfile = new DistributorDTO();
        coreProfile.setName("Acme Corp");
        coreProfile.setIsActive(false);

        when(coreDistributorApi.getDistributorById(distributorId)).thenReturn(Mono.just(coreProfile));
        when(termsAndConditionsService.hasActiveSignedTerms(distributorId)).thenReturn(Mono.just(false));
        when(brandingService.listBrandings(distributorId)).thenReturn(Flux.empty());

        StepVerifier.create(service.getDistributorDetail(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getStatus()).isEqualTo("INACTIVE");
                    assertThat(dto.getHasActiveSignedTerms()).isFalse();
                    assertThat(dto.getActiveBranding()).isNotNull(); // defaultIfEmpty produces empty builder result
                })
                .verifyComplete();
    }

    @Test
    void updateDistributor_shouldCallCoreApiAndReturnRefreshedDetail() {
        UUID distributorId = UUID.randomUUID();
        UpdateDistributorRequest request = UpdateDistributorRequest.builder()
                .name("Acme Corp Updated")
                .displayName("Acme Updated")
                .email("updated@acme.com")
                .build();

        DistributorDTO updatedProfile = new DistributorDTO();
        updatedProfile.setName("Acme Corp Updated");
        updatedProfile.setIsActive(true);

        when(coreDistributorApi.updateDistributor(eq(distributorId), any()))
                .thenReturn(Mono.just(updatedProfile));
        when(coreDistributorApi.getDistributorById(distributorId)).thenReturn(Mono.just(updatedProfile));
        when(termsAndConditionsService.hasActiveSignedTerms(distributorId)).thenReturn(Mono.just(false));
        when(brandingService.listBrandings(distributorId)).thenReturn(Flux.empty());

        StepVerifier.create(service.updateDistributor(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(distributorId);
                    assertThat(dto.getName()).isEqualTo("Acme Corp Updated");
                })
                .verifyComplete();

        verify(coreDistributorApi).updateDistributor(eq(distributorId), any());
    }

    @Test
    void deleteDistributor_shouldCallDeleteOnCoreApi() {
        UUID distributorId = UUID.randomUUID();
        when(coreDistributorApi.deleteDistributor(distributorId)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteDistributor(distributorId))
                .verifyComplete();

        verify(coreDistributorApi).deleteDistributor(distributorId);
    }
}
