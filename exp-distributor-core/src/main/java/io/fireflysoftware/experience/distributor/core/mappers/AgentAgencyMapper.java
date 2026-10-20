package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.branding.sdk.model.AssignAgentAgencyCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AssignAgentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgentAgencyMapper {

    AssignAgentAgencyCommand toCommand(AssignAgentRequest request);
}
