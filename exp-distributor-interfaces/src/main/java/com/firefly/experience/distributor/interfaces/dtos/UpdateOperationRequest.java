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
public class UpdateOperationRequest {

    private UUID countryId;
    private UUID administrativeDivisionId;
    private UUID managedByAgentId;
    private UUID agencyId;
}
