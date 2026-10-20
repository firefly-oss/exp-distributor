package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateTermsAndConditionsCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorTermsAndConditionsDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateTermsRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.TermsDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TermsMapper {

    TermsDTO toDto(DistributorTermsAndConditionsDTO sdk);

    CreateTermsAndConditionsCommand toCreateCommand(CreateTermsRequest request);

    CreateTermsAndConditionsCommand toUpdateCommand(UpdateTermsRequest request);
}
