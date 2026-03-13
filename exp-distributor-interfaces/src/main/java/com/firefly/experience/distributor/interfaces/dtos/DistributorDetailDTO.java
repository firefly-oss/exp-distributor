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
public class DistributorDetailDTO {

    private UUID id;
    private String name;
    private String displayName;
    private String email;
    private String status;
    private BrandingDTO activeBranding;
    private Boolean hasActiveSignedTerms;
}
