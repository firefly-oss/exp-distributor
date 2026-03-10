package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.branding.sdk.model.CreateAgentCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorAgentDTO;
import com.firefly.domain.distributor.branding.sdk.model.UpdateAgentCommand;
import com.firefly.experience.distributor.interfaces.dtos.AgentDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgentRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgentMapper {

    AgentDTO toDto(DistributorAgentDTO sdk);

    CreateAgentCommand toCreateCommand(CreateAgentRequest request);

    UpdateAgentCommand toUpdateCommand(UpdateAgentRequest request);
}
