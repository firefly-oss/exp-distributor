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
public class CatalogItemDTO {

    private UUID id;
    private UUID distributorId;
    private UUID productId;
    private String productName;
    private String status;
    private Boolean isActive;
}
