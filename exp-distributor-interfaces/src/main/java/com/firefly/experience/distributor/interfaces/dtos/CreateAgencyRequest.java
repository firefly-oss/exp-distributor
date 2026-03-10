package com.firefly.experience.distributor.interfaces.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgencyRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String city;

    private String state;

    private Boolean isActive;

    private Boolean isHeadquarters;
}
