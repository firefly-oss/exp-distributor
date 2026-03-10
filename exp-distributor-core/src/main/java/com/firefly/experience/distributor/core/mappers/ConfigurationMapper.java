package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.branding.sdk.model.CreateConfigurationCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorConfigurationDTO;
import com.firefly.domain.distributor.branding.sdk.model.UpdateConfigurationCommand;
import com.firefly.experience.distributor.interfaces.dtos.ConfigurationDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateConfigurationRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateConfigurationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigurationMapper {

    ConfigurationDTO toDto(DistributorConfigurationDTO sdk);

    CreateConfigurationCommand toCreateCommand(CreateConfigurationRequest request);

    UpdateConfigurationCommand toUpdateCommand(UpdateConfigurationRequest request);
}
