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
public class RegisterProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String category;

    private String status;
}
