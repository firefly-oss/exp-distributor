package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateOperationCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorOperationDTO;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateOperationCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateOperationRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.OperationDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateOperationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationDTO toDto(DistributorOperationDTO sdk);

    CreateOperationCommand toCreateCommand(CreateOperationRequest request);

    UpdateOperationCommand toUpdateCommand(UpdateOperationRequest request);
}
