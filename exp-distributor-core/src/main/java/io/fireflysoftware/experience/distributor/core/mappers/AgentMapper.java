package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateAgentCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorAgentDTO;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateAgentCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AgentDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateAgentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateAgentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgentMapper {

    AgentDTO toDto(DistributorAgentDTO sdk);

    CreateAgentCommand toCreateCommand(CreateAgentRequest request);

    UpdateAgentCommand toUpdateCommand(UpdateAgentRequest request);
}
