package com.firefly.experience.distributor.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShipmentRequest {

    private String trackingNumber;

    private String carrier;

    private String shippingAddress;

    private String status;

    private LocalDateTime estimatedDelivery;
}
