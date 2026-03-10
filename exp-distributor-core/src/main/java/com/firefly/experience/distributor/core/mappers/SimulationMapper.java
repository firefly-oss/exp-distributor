package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.catalog.sdk.model.CreateSimulationCommand;
import com.firefly.domain.distributor.catalog.sdk.model.DistributorSimulationDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateSimulationRequest;
import com.firefly.experience.distributor.interfaces.dtos.SimulationResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SimulationMapper {

    @Mapping(target = "status", source = "simulationStatus")
    SimulationResultDTO toDto(DistributorSimulationDTO sdk);

    @Mapping(target = "distributorId", ignore = true)
    CreateSimulationCommand toCommand(CreateSimulationRequest request);
}
