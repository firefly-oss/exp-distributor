package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.catalog.sdk.model.CreateSimulationCommand;
import io.fireflysoftware.domain.distributor.catalog.sdk.model.DistributorSimulationDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.SimulationResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SimulationMapper {

    @Mapping(target = "status", source = "simulationStatus")
    SimulationResultDTO toDto(DistributorSimulationDTO sdk);

    @Mapping(target = "distributorId", ignore = true)
    CreateSimulationCommand toCommand(CreateSimulationRequest request);
}
