package com.firefly.experience.distributor.core.branding;

import com.firefly.core.distributor.sdk.api.DistributorBrandingApi;
import com.firefly.core.distributor.sdk.model.DistributorBrandingDTO;
import com.firefly.domain.distributor.branding.sdk.api.DistributorApi;
import com.firefly.domain.distributor.branding.sdk.model.ReviseBrandingCommand;
import com.firefly.domain.distributor.branding.sdk.model.SetDefaultBrandingCommand;
import com.firefly.experience.distributor.interfaces.dtos.BrandingDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateBrandingRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateBrandingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandingServiceImplTest {

    @Mock
    private DistributorApi brandingDistributorApi;

    @Mock
    private DistributorBrandingApi distributorBrandingApi;

    @InjectMocks
    private BrandingServiceImpl service;

    private DistributorBrandingDTO buildSdkBrandingDTO(UUID distributorId, UUID brandingId) {
        DistributorBrandingDTO dto = new DistributorBrandingDTO(brandingId);
        dto.setDistributorId(distributorId);
        dto.setLogoUrl("https://example.com/logo.png");
        dto.setPrimaryColor("#FF0000");
        dto.setSecondaryColor("#00FF00");
        dto.setFontFamily("Arial");
        dto.setIsDefault(false);
        return dto;
    }

    @Test
    void createBranding_shouldCallCoreApiAndReturnDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        CreateBrandingRequest request = CreateBrandingRequest.builder()
                .logoUrl("https://example.com/logo.png")
                .primaryColor("#FF0000")
                .build();

        DistributorBrandingDTO sdkResponse = buildSdkBrandingDTO(distributorId, brandingId);

        when(distributorBrandingApi.createDistributorBranding(eq(distributorId), any()))
                .thenReturn(Mono.just(sdkResponse));

        StepVerifier.create(service.createBranding(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(brandingId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getLogoUrl()).isEqualTo("https://example.com/logo.png");
                    assertThat(dto.getPrimaryColor()).isEqualTo("#FF0000");
                })
                .verifyComplete();

        verify(distributorBrandingApi).createDistributorBranding(eq(distributorId), any());
    }

    @Test
    void getBranding_shouldCallGetByIdOnCoreApi() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        DistributorBrandingDTO sdkResponse = buildSdkBrandingDTO(distributorId, brandingId);

        when(distributorBrandingApi.getDistributorBrandingById(distributorId, brandingId))
                .thenReturn(Mono.just(sdkResponse));

        StepVerifier.create(service.getBranding(distributorId, brandingId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(brandingId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();
    }

    @Test
    void updateBranding_shouldCallCommandSdkThenFetchUpdatedProjection() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        UpdateBrandingRequest request = UpdateBrandingRequest.builder()
                .logoUrl("https://example.com/new-logo.png")
                .primaryColor("#0000FF")
                .build();

        DistributorBrandingDTO updatedSdk = buildSdkBrandingDTO(distributorId, brandingId);
        updatedSdk.setLogoUrl("https://example.com/new-logo.png");

        when(brandingDistributorApi.reviseBranding(eq(distributorId), eq(brandingId), any(ReviseBrandingCommand.class)))
                .thenReturn(Mono.just(brandingId));
        when(distributorBrandingApi.getDistributorBrandingById(distributorId, brandingId))
                .thenReturn(Mono.just(updatedSdk));

        StepVerifier.create(service.updateBranding(distributorId, brandingId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(brandingId);
                    assertThat(dto.getLogoUrl()).isEqualTo("https://example.com/new-logo.png");
                })
                .verifyComplete();

        verify(brandingDistributorApi).reviseBranding(eq(distributorId), eq(brandingId), any(ReviseBrandingCommand.class));
        verify(distributorBrandingApi).getDistributorBrandingById(distributorId, brandingId);
    }

    @Test
    void deleteBranding_shouldCallDeleteOnCoreApi() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();
        when(distributorBrandingApi.deleteDistributorBranding(distributorId, brandingId))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.deleteBranding(distributorId, brandingId))
                .verifyComplete();

        verify(distributorBrandingApi).deleteDistributorBranding(distributorId, brandingId);
    }

    @Test
    void setDefaultBranding_shouldCallCommandSdkThenFetchUpdatedProjection() {
        UUID distributorId = UUID.randomUUID();
        UUID brandingId = UUID.randomUUID();

        DistributorBrandingDTO defaultBranding = buildSdkBrandingDTO(distributorId, brandingId);
        defaultBranding.setIsDefault(true);

        when(brandingDistributorApi.setDefaultBranding(eq(distributorId), eq(brandingId), any(SetDefaultBrandingCommand.class)))
                .thenReturn(Mono.just(brandingId));
        when(distributorBrandingApi.getDistributorBrandingById(distributorId, brandingId))
                .thenReturn(Mono.just(defaultBranding));

        StepVerifier.create(service.setDefaultBranding(distributorId, brandingId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(brandingId);
                    assertThat(dto.getIsDefault()).isTrue();
                })
                .verifyComplete();

        verify(brandingDistributorApi).setDefaultBranding(eq(distributorId), eq(brandingId), any(SetDefaultBrandingCommand.class));
    }
}
