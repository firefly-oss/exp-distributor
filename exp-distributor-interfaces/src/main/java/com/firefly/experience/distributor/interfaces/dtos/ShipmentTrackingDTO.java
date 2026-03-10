package com.firefly.experience.distributor.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentTrackingDTO {

    private UUID shipmentId;
    private String trackingNumber;
    private String carrier;
    private String currentStatus;
    private List<String> events;
}
