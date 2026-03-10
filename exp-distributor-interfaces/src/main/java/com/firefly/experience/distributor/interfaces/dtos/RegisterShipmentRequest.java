package com.firefly.experience.distributor.interfaces.dtos;

import jakarta.validation.constraints.NotNull;
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
public class RegisterShipmentRequest {

    @NotNull
    private UUID productId;

    private String trackingNumber;

    private String carrier;

    private String shippingAddress;

    private LocalDateTime estimatedDelivery;
}
