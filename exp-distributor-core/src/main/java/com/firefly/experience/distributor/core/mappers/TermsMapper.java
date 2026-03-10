package com.firefly.experience.distributor.core.mappers;

import com.firefly.domain.distributor.branding.sdk.model.CreateTermsAndConditionsCommand;
import com.firefly.domain.distributor.branding.sdk.model.DistributorTermsAndConditionsDTO;
import com.firefly.experience.distributor.interfaces.dtos.CreateTermsRequest;
import com.firefly.experience.distributor.interfaces.dtos.TermsDTO;
import com.firefly.experience.distributor.interfaces.dtos.UpdateTermsRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TermsMapper {

    TermsDTO toDto(DistributorTermsAndConditionsDTO sdk);

    CreateTermsAndConditionsCommand toCreateCommand(CreateTermsRequest request);

    CreateTermsAndConditionsCommand toUpdateCommand(UpdateTermsRequest request);
}
