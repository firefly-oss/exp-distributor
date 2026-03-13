package com.firefly.experience.distributor.core.network;

import com.firefly.domain.distributor.branding.sdk.api.TerritoryApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateTerritoryCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorAuthorizedTerritoryDTO;
import com.firefly.domain.distributor.branding.sdk.model.PaginationResponse;
import com.firefly.domain.distributor.branding.sdk.model.UpdateTerritoryCommand;
import com.firefly.experience.distributor.core.mappers.TerritoryMapper;
import com.firefly.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import com.firefly.experience.distributor.interfaces.dtos.TerritoryDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
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
class TerritoryServiceImplTest {

    @Mock
    private TerritoryApi territoryApi;

    @Mock
    private TerritoryMapper territoryMapper;

    @InjectMocks
    private TerritoryServiceImpl service;

    private DistributorAuthorizedTerritoryDTO buildSdkTerritory(UUID distributorId, UUID territoryId) {
        DistributorAuthorizedTerritoryDTO dto = new DistributorAuthorizedTerritoryDTO();
        dto.setId(territoryId);
        dto.setDistributorId(distributorId);
        dto.setCountryId(UUID.randomUUID());
        dto.setAuthorizationLevel(DistributorAuthorizedTerritoryDTO.AuthorizationLevelEnum.COUNTRY);
        dto.setIsActive(true);
        return dto;
    }

    private TerritoryDTO buildTerritoryDTO(UUID distributorId, UUID territoryId) {
        return TerritoryDTO.builder()
                .id(territoryId)
                .distributorId(distributorId)
                .authorizationLevel("COUNTRY")
                .isActive(true)
                .build();
    }

    @Test
    void createTerritory_shouldMapRequestToCommandAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();
        CreateTerritoryRequest request = CreateTerritoryRequest.builder()
                .countryId(UUID.randomUUID())
                .authorizationLevel("COUNTRY")
                .isActive(true)
                .build();
        CreateTerritoryCommand command = new CreateTerritoryCommand();

        when(territoryMapper.toCreateCommand(request)).thenReturn(command);
        when(territoryApi.createTerritory(distributorId, command)).thenReturn(Mono.just(territoryId));

        StepVerifier.create(service.createTerritory(distributorId, request))
                .assertNext(id -> assertThat(id).isEqualTo(territoryId))
                .verifyComplete();

        verify(territoryMapper).toCreateCommand(request);
        verify(territoryApi).createTerritory(distributorId, command);
    }

    @Test
    void getTerritory_shouldCallApiAndMapToDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();
        DistributorAuthorizedTerritoryDTO sdkDto = buildSdkTerritory(distributorId, territoryId);
        TerritoryDTO expected = buildTerritoryDTO(distributorId, territoryId);

        when(territoryApi.getTerritory(distributorId, territoryId)).thenReturn(Mono.just(sdkDto));
        when(territoryMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.getTerritory(distributorId, territoryId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(territoryId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getAuthorizationLevel()).isEqualTo("COUNTRY");
                })
                .verifyComplete();

        verify(territoryApi).getTerritory(distributorId, territoryId);
        verify(territoryMapper).toDto(sdkDto);
    }

    @Test
    void listTerritories_shouldDelegateToApiAndReturnPaginationResponse() {
        UUID distributorId = UUID.randomUUID();
        PaginationResponse response = new PaginationResponse();

        when(territoryApi.listTerritories(distributorId)).thenReturn(Mono.just(response));

        StepVerifier.create(service.listTerritories(distributorId))
                .assertNext(r -> assertThat(r).isSameAs(response))
                .verifyComplete();

        verify(territoryApi).listTerritories(distributorId);
    }

    @Test
    void updateTerritory_shouldMapRequestToCommandAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();
        UpdateTerritoryRequest request = UpdateTerritoryRequest.builder()
                .authorizationLevel("REGION")
                .isActive(false)
                .build();
        UpdateTerritoryCommand command = new UpdateTerritoryCommand();

        when(territoryMapper.toUpdateCommand(request)).thenReturn(command);
        when(territoryApi.updateTerritory(distributorId, territoryId, command))
                .thenReturn(Mono.just(territoryId));

        StepVerifier.create(service.updateTerritory(distributorId, territoryId, request))
                .assertNext(id -> assertThat(id).isEqualTo(territoryId))
                .verifyComplete();

        verify(territoryMapper).toUpdateCommand(request);
        verify(territoryApi).updateTerritory(distributorId, territoryId, command);
    }

    @Test
    void deleteTerritory_shouldDelegateToApiAndComplete() {
        UUID distributorId = UUID.randomUUID();
        UUID territoryId = UUID.randomUUID();

        when(territoryApi.deleteTerritory(distributorId, territoryId)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteTerritory(distributorId, territoryId))
                .verifyComplete();

        verify(territoryApi).deleteTerritory(distributorId, territoryId);
    }
}
