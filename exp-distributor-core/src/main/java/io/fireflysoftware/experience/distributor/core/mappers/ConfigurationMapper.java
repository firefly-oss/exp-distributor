package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateConfigurationCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorConfigurationDTO;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateConfigurationCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.ConfigurationDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigurationMapper {

    ConfigurationDTO toDto(DistributorConfigurationDTO sdk);

    CreateConfigurationCommand toCreateCommand(CreateConfigurationRequest request);

    UpdateConfigurationCommand toUpdateCommand(UpdateConfigurationRequest request);
}
