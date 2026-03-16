package com.firefly.experience.distributor.core.network;

import com.firefly.domain.distributor.branding.sdk.api.AgencyApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateAgencyCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorAgencyDTO;
import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.domain.distributor.branding.sdk.model.UpdateAgencyCommand;
import com.firefly.experience.distributor.core.mappers.AgencyMapper;
import com.firefly.experience.distributor.interfaces.dtos.AgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgencyServiceImplTest {

    @Mock
    private AgencyApi agencyApi;

    @Mock
    private AgencyMapper agencyMapper;

    @InjectMocks
    private AgencyServiceImpl service;

    private DistributorAgencyDTO buildSdkAgency(UUID distributorId, UUID agencyId) {
        DistributorAgencyDTO dto = new DistributorAgencyDTO();
        dto.setId(agencyId);
        dto.setDistributorId(distributorId);
        dto.setName("Main Office");
        dto.setCode("MO-001");
        dto.setCity("Madrid");
        dto.setState("Madrid");
        dto.setIsActive(true);
        dto.setIsHeadquarters(true);
        return dto;
    }

    private AgencyDTO buildAgencyDTO(UUID distributorId, UUID agencyId) {
        return AgencyDTO.builder()
                .id(agencyId)
                .distributorId(distributorId)
                .name("Main Office")
                .code("MO-001")
                .city("Madrid")
                .state("Madrid")
                .isActive(true)
                .isHeadquarters(true)
                .build();
    }

    @Test
    void createAgency_shouldMapRequestToCommandAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();
        CreateAgencyRequest request = CreateAgencyRequest.builder()
                .name("Main Office")
                .code("MO-001")
                .city("Madrid")
                .isActive(true)
                .isHeadquarters(true)
                .build();
        CreateAgencyCommand command = new CreateAgencyCommand();

        when(agencyMapper.toCreateCommand(request)).thenReturn(command);
        when(agencyApi.createAgency(eq(distributorId), eq(command), any())).thenReturn(Mono.just(agencyId));

        StepVerifier.create(service.createAgency(distributorId, request))
                .assertNext(id -> assertThat(id).isEqualTo(agencyId))
                .verifyComplete();

        verify(agencyMapper).toCreateCommand(request);
        verify(agencyApi).createAgency(eq(distributorId), eq(command), any());
    }

    @Test
    void getAgency_shouldCallApiAndMapToDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();
        DistributorAgencyDTO sdkDto = buildSdkAgency(distributorId, agencyId);
        AgencyDTO expected = buildAgencyDTO(distributorId, agencyId);

        when(agencyApi.getAgency(eq(distributorId), eq(agencyId), any())).thenReturn(Mono.just(sdkDto));
        when(agencyMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.getAgency(distributorId, agencyId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(agencyId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getName()).isEqualTo("Main Office");
                    assertThat(dto.getIsHeadquarters()).isTrue();
                })
                .verifyComplete();

        verify(agencyApi).getAgency(eq(distributorId), eq(agencyId), any());
        verify(agencyMapper).toDto(sdkDto);
    }

    @Test
    void listAgencies_shouldDelegateToApiAndReturnPaginationResponse() {
        UUID distributorId = UUID.randomUUID();
        PaginationResponse response = new PaginationResponse();

        when(agencyApi.listAgencies(eq(distributorId), any())).thenReturn(Mono.just(response));

        StepVerifier.create(service.listAgencies(distributorId))
                .assertNext(r -> assertThat(r).isSameAs(response))
                .verifyComplete();

        verify(agencyApi).listAgencies(eq(distributorId), any());
    }

    @Test
    void updateAgency_shouldMapRequestToCommandAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();
        UpdateAgencyRequest request = UpdateAgencyRequest.builder()
                .name("Updated Office")
                .city("Barcelona")
                .isActive(false)
                .build();
        UpdateAgencyCommand command = new UpdateAgencyCommand();

        when(agencyMapper.toUpdateCommand(request)).thenReturn(command);
        when(agencyApi.updateAgency(eq(distributorId), eq(agencyId), eq(command), any())).thenReturn(Mono.just(agencyId));

        StepVerifier.create(service.updateAgency(distributorId, agencyId, request))
                .assertNext(id -> assertThat(id).isEqualTo(agencyId))
                .verifyComplete();

        verify(agencyMapper).toUpdateCommand(request);
        verify(agencyApi).updateAgency(eq(distributorId), eq(agencyId), eq(command), any());
    }

    @Test
    void deleteAgency_shouldDelegateToApiAndComplete() {
        UUID distributorId = UUID.randomUUID();
        UUID agencyId = UUID.randomUUID();

        when(agencyApi.deleteAgency(eq(distributorId), eq(agencyId), any())).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteAgency(distributorId, agencyId))
                .verifyComplete();

        verify(agencyApi).deleteAgency(eq(distributorId), eq(agencyId), any());
    }
}
