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
public class AgencyDTO {

    private UUID id;
    private UUID distributorId;
    private String name;
    private String code;
    private String city;
    private String state;
    private Boolean isActive;
    private Boolean isHeadquarters;
}
