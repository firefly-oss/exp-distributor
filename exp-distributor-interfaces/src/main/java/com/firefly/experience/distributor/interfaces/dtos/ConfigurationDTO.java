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
public class ConfigurationDTO {

    private UUID id;
    private UUID distributorId;
    private String configKey;
    private String configValue;
    private String category;
    private String description;
    private Boolean isActive;
}
