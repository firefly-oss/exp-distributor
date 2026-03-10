package com.firefly.experience.distributor.interfaces.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDistributorRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String displayName;

    @NotBlank
    private String email;

    private String phoneNumber;

    private String taxId;
}
