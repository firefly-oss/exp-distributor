package com.firefly.experience.distributor.interfaces.dtos;

import jakarta.validation.constraints.NotNull;
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
public class CreateTerritoryRequest {

    @NotNull
    private UUID countryId;

    private String authorizationLevel;

    private Boolean isActive;

    private LocalDateTime authorizedFrom;

    private LocalDateTime authorizedUntil;
}
