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

import io.fireflysoftware.domain.distributor.branding.sdk.model.CreateAgentCommand;
import io.fireflysoftware.domain.distributor.branding.sdk.model.DistributorAgentDTO;
import io.fireflysoftware.domain.distributor.branding.sdk.model.UpdateAgentCommand;
import io.fireflysoftware.experience.distributor.interfaces.dtos.AgentDTO;
import io.fireflysoftware.experience.distributor.interfaces.dtos.CreateAgentRequest;
import io.fireflysoftware.experience.distributor.interfaces.dtos.UpdateAgentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgentMapper {

    AgentDTO toDto(DistributorAgentDTO sdk);

    CreateAgentCommand toCreateCommand(CreateAgentRequest request);

    UpdateAgentCommand toUpdateCommand(UpdateAgentRequest request);
}
