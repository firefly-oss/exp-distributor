/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
