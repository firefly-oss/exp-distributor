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
public class AddCatalogItemRequest {

    @NotNull
    private UUID productId;

    private String productName;
    private String status;
}
