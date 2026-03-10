package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.branding.sdk.model.AssignAgentAgencyCommand;
import com.firefly.experience.distributor.interfaces.dtos.AssignAgentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgentAgencyMapper {

    AssignAgentAgencyCommand toCommand(AssignAgentRequest request);
}
