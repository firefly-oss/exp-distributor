package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.branding.sdk.model.CreateAgencyCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorAgencyDTO;
import com.firefly.domain.distributor.branding.sdk.model.UpdateAgencyCommand;
import com.firefly.experience.distributor.interfaces.dtos.AgencyDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgencyMapper {

    AgencyDTO toDto(DistributorAgencyDTO sdk);

    CreateAgencyCommand toCreateCommand(CreateAgencyRequest request);

    UpdateAgencyCommand toUpdateCommand(UpdateAgencyRequest request);
}
