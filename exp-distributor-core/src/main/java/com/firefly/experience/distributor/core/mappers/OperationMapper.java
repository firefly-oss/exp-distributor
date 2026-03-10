package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.branding.sdk.model.CreateOperationCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorOperationDTO;
import com.firefly.domain.distributor.branding.sdk.model.UpdateOperationCommand;
import com.firefly.experience.distributor.interfaces.dtos.CreateOperationRequest;
import com.firefly.experience.distributor.interfaces.dtos.OperationDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateOperationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationDTO toDto(DistributorOperationDTO sdk);

    CreateOperationCommand toCreateCommand(CreateOperationRequest request);

    UpdateOperationCommand toUpdateCommand(UpdateOperationRequest request);
}
