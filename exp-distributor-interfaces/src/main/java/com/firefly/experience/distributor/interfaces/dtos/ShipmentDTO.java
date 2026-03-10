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
public class ShipmentDTO {

    private UUID id;
    private UUID distributorId;
    private UUID productId;
    private String trackingNumber;
    private String carrier;
    private String status;
    private LocalDateTime estimatedDelivery;
}
