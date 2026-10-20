package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateAgencyCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorAgencyDTO;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateAgencyCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AgencyDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateAgencyRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateAgencyRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgencyMapper {

    AgencyDTO toDto(DistributorAgencyDTO sdk);

    CreateAgencyCommand toCreateCommand(CreateAgencyRequest request);

    UpdateAgencyCommand toUpdateCommand(UpdateAgencyRequest request);
}
