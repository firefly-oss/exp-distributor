package com.firefly.experience.distributor.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentAgencyDTO {

    private UUID id;
    private UUID agentId;
    private UUID agencyId;
    private UUID roleId;
    private Boolean isPrimaryAgency;
    private Boolean isActive;
}
