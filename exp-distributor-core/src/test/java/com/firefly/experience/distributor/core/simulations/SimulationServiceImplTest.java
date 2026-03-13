package com.firefly.experience.distributor.core.simulations;

import com.firefly.domain.distributor.catalog.sdk.api.SimulationsApi;
import com.firefly.domain.distributor.catalog.sdk.model.CreateSimulationCommand;
import com.firefly.domain.distributor.catalog.sdk.model.DistributorSimulationDTO;
import com.firefly.experience.distributor.core.mappers.SimulationMapper;
import com.firefly.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import com.firefly.experience.distributor.interfaces.dtos.SimulationResultDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationServiceImplTest {

    @Mock
    private SimulationsApi simulationsApi;

    @Mock
    private SimulationMapper simulationMapper;

    @InjectMocks
    private SimulationServiceImpl service;

    private DistributorSimulationDTO buildSdkSimulation(UUID distributorId, UUID simulationId) {
        DistributorSimulationDTO dto = new DistributorSimulationDTO();
        dto.setId(simulationId);
        dto.setDistributorId(distributorId);
        dto.setSimulationStatus("COMPLETED");
        return dto;
    }

    private SimulationResultDTO buildSimulationResultDTO(UUID distributorId, UUID simulationId) {
        return SimulationResultDTO.builder()
                .id(simulationId)
                .distributorId(distributorId)
                .status("COMPLETED")
                .build();
    }

    @Test
    void createSimulation_shouldSetDistributorIdOnCommandAndReturnId() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();
        CreateSimulationRequest request = CreateSimulationRequest.builder()
                .agentId(UUID.randomUUID())
                .agencyId(UUID.randomUUID())
                .build();
        // The mapper produces a command with distributorId ignored; the impl sets it explicitly
        CreateSimulationCommand command = new CreateSimulationCommand();

        when(simulationMapper.toCommand(request)).thenReturn(command);
        when(simulationsApi.createSimulation(distributorId, command)).thenReturn(Mono.just(simulationId));

        StepVerifier.create(service.createSimulation(distributorId, request))
                .assertNext(id -> assertThat(id).isEqualTo(simulationId))
                .verifyComplete();

        // Verify the impl mutated the command's distributorId before calling the API
        assertThat(command.getDistributorId()).isEqualTo(distributorId);

        verify(simulationMapper).toCommand(request);
        verify(simulationsApi).createSimulation(distributorId, command);
    }

    @Test
    void getSimulation_shouldCallApiAndMapToResultDto() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();
        DistributorSimulationDTO sdkDto = buildSdkSimulation(distributorId, simulationId);
        SimulationResultDTO expected = buildSimulationResultDTO(distributorId, simulationId);

        when(simulationsApi.getSimulation(distributorId, simulationId)).thenReturn(Mono.just(sdkDto));
        when(simulationMapper.toDto(sdkDto)).thenReturn(expected);

        StepVerifier.create(service.getSimulation(distributorId, simulationId))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(simulationId);
                    assertThat(dto.getDistributorId()).isEqualTo(distributorId);
                    assertThat(dto.getStatus()).isEqualTo("COMPLETED");
                })
                .verifyComplete();

        verify(simulationsApi).getSimulation(distributorId, simulationId);
        verify(simulationMapper).toDto(sdkDto);
    }
}
