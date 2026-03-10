package com.firefly.experience.distributor.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgencyRequest {

    private String name;

    private String code;

    private String city;

    private String state;

    private Boolean isActive;

    private Boolean isHeadquarters;
}
