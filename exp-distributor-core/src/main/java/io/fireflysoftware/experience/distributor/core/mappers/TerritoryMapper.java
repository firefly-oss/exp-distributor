package io.fireflysoftware.experience.distributor.core.mappers;

import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateTerritoryCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorAuthorizedTerritoryDTO;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateTerritoryCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateTerritoryRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.TerritoryDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateTerritoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TerritoryMapper {

    @Mapping(target = "authorizationLevel", source = "authorizationLevel", qualifiedByName = "authLevelToString")
    TerritoryDTO toDto(DistributorAuthorizedTerritoryDTO sdk);

    @Mapping(target = "authorizationLevel", source = "authorizationLevel", qualifiedByName = "stringToCreateAuthLevel")
    CreateTerritoryCommand toCreateCommand(CreateTerritoryRequest request);

    @Mapping(target = "authorizationLevel", source = "authorizationLevel", qualifiedByName = "stringToUpdateAuthLevel")
    UpdateTerritoryCommand toUpdateCommand(UpdateTerritoryRequest request);

    @Named("authLevelToString")
    default String authLevelToString(DistributorAuthorizedTerritoryDTO.AuthorizationLevelEnum level) {
        return level != null ? level.getValue() : null;
    }

    @Named("stringToCreateAuthLevel")
    default CreateTerritoryCommand.AuthorizationLevelEnum stringToCreateAuthLevel(String level) {
        return level != null ? CreateTerritoryCommand.AuthorizationLevelEnum.fromValue(level) : null;
    }

    @Named("stringToUpdateAuthLevel")
    default UpdateTerritoryCommand.AuthorizationLevelEnum stringToUpdateAuthLevel(String level) {
        return level != null ? UpdateTerritoryCommand.AuthorizationLevelEnum.fromValue(level) : null;
    }
}
