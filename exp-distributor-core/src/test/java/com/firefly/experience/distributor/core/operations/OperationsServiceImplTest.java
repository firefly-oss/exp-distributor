package com.firefly.experience.distributor.core.operations;

import com.firefly.domain.distributor.branding.sdk.api.OperationApi;
import com.firefly.domain.distributor.branding.sdk.model.CreateOperationCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorOperationDTO;
import com.firefly.domain.distributor.branding.sdk.model.UpdateOperationCommand;
import com.firefly.experience.distributor.core.mappers.OperationMapper;
import com.firefly.experience.distributor.interfaces.dtos.CreateOperationRequest;
import com.firefly.experience.distributor.interfaces.dtos.OperationDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateOperationRequest;
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
class OperationsServiceImplTest {

    @Mock
    private OperationApi operationApi;

    @Mock
    private OperationMapper operationMapper;

    @InjectMocks
    private OperationsServiceImpl service;

    private DistributorOperationDTO buildSdkOperation(UUID distributorId, UUID operationId) {
        DistributorOperationDTO dto = new DistributorOperationDTO();
        dto.setId(operationId);
        dto.setDistributorId(distributorId);
        dto.setCountryId(UUID.randomUUID());
        dto.setIsActive(true);
        return dto;
    }

    private OperationDTO buildOperationDTO(UUID distributorId, UUID operationId) {
        return OperationDTO.builder()
                .id(operationId)
                .distributorId(distributorId)
                .isActive(true)
                .build();
    }

    @Test
    void listOperations_shouldCallApiAndEmitMappedDto() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        DistributorOperationDTO sdkDto = buildSdkOperation(distributorId, operationId);
        OperationDTO expected = buildOperationDTO(distributorId, operationId);

        when(operationApi.listOperations(distributorId)).thenReturn(Mono.just(sdkDto));
        when(operationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.listOperations(distributorId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(operationId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getIsActive()).isTrue();
                })
                .verifyComplete();

        verify(operationApi).listOperations(distributorId);
        verify(operationMapper).toDto(sdkDto);
    }

    @Test
    void listOperations_whenApiReturnsEmpty_shouldCompleteWithNoElements() {
        UUID distributorId = UUID.randomUUID();
        when(operationApi.listOperations(distributorId)).thenReturn(Mono.empty());

        StepVerifier.create(service.listOperations(distributorId))
                .verifyComplete();

        verify(operationApi).listOperations(distributorId);
    }

    @Test
    void createOperation_shouldMapRequestFetchAndReturnDto() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        CreateOperationRequest request = CreateOperationRequest.builder()
                .countryId(UUID.randomUUID())
                .administrativeDivisionId(UUID.randomUUID())
                .build();
        CreateOperationCommand command = new CreateOperationCommand();
        DistributorOperationDTO sdkDto = buildSdkOperation(distributorId, operationId);
        OperationDTO expected = buildOperationDTO(distributorId, operationId);

        when(operationMapper.toCreateCommand(request)).thenReturn(command);
        when(operationApi.createOperation(distributorId, command)).thenReturn(Mono.just(operationId));
        when(operationApi.getOperation(distributorId, operationId)).thenReturn(Mono.just(sdkDto));
        when(operationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.createOperation(distributorId, request))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(operationId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                })
                .verifyComplete();

        verify(operationMapper).toCreateCommand(request);
        verify(operationApi).createOperation(distributorId, command);
        verify(operationApi).getOperation(distributorId, operationId);
        verify(operationMapper).toDto(sdkDto);
    }

    @Test
    void updateOperation_shouldMapRequestFetchAndReturnDto() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        UpdateOperationRequest request = UpdateOperationRequest.builder()
                .countryId(UUID.randomUUID())
                .agencyId(UUID.randomUUID())
                .build();
        UpdateOperationCommand command = new UpdateOperationCommand();
        DistributorOperationDTO sdkDto = buildSdkOperation(distributorId, operationId);
        OperationDTO expected = buildOperationDTO(distributorId, operationId);

        when(operationMapper.toUpdateCommand(request)).thenReturn(command);
        when(operationApi.updateOperation(distributorId, operationId, command)).thenReturn(Mono.just(operationId));
        when(operationApi.getOperation(distributorId, operationId)).thenReturn(Mono.just(sdkDto));
        when(operationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.updateOperation(distributorId, operationId, request))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(operationId))
                .verifyComplete();

        verify(operationMapper).toUpdateCommand(request);
        verify(operationApi).updateOperation(distributorId, operationId, command);
        verify(operationApi).getOperation(distributorId, operationId);
    }

    @Test
    void deleteOperation_shouldDelegateToApiAndComplete() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        when(operationApi.deleteOperation(distributorId, operationId)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteOperation(distributorId, operationId))
                .verifyComplete();

        verify(operationApi).deleteOperation(distributorId, operationId);
    }

    @Test
    void activateOperation_shouldPassIdempotencyKeyAndMapResult() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        DistributorOperationDTO sdkDto = buildSdkOperation(distributorId, operationId);
        OperationDTO expected = buildOperationDTO(distributorId, operationId);

        // The impl passes UUID.randomUUID() as idempotency key — match with any(UUID.class)
        when(operationApi.activateOperation(eq(distributorId), eq(operationId), any(UUID.class)))
                .thenReturn(Mono.just(sdkDto));
        when(operationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.activateOperation(distributorId, operationId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(operationId);
                    assertThat(dto.getIsActive()).isTrue();
                })
                .verifyComplete();

        verify(operationApi).activateOperation(eq(distributorId), eq(operationId), any(UUID.class));
        verify(operationMapper).toDto(sdkDto);
    }

    @Test
    void deactivateOperation_shouldPassIdempotencyKeyAndMapResult() {
        UUID distributorId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        DistributorOperationDTO sdkDto = buildSdkOperation(distributorId, operationId);
        OperationDTO expected = buildOperationDTO(distributorId, operationId);

        when(operationApi.deactivateOperation(eq(distributorId), eq(operationId), any(UUID.class)))
                .thenReturn(Mono.just(sdkDto));
        when(operationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.deactivateOperation(distributorId, operationId))
                .assertNext(dto -> assertThat(dto.getId()).isEqualTo(operationId))
                .verifyComplete();

        verify(operationApi).deactivateOperation(eq(distributorId), eq(operationId), any(UUID.class));
    }

    @Test
    void canOperate_shouldDelegateToApiAndReturnBoolean() {
        UUID distributorId = UUID.randomUUID();

        when(operationApi.canOperate(distributorId, distributorId, distributorId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(service.canOperate(distributorId))
                .assertNext(result -> assertThat(result).isTrue())
                .verifyComplete();

        verify(operationApi).canOperate(distributorId, distributorId, distributorId);
    }
}
