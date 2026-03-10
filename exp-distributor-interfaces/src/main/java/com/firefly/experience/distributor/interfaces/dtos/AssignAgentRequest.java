package com.firefly.experience.distributor.interfaces.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignAgentRequest {

    @NotNull
    private UUID agentId;

    @NotNull
    private UUID agencyId;

    private UUID roleId;

    private Boolean isPrimaryAgency;
}
