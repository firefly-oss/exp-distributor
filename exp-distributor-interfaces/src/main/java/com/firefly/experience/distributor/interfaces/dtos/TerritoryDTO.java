package com.firefly.experience.distributor.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerritoryDTO {

    private UUID id;
    private UUID distributorId;
    private UUID countryId;
    private String authorizationLevel;
    private Boolean isActive;
    private LocalDateTime authorizedFrom;
    private LocalDateTime authorizedUntil;
}
