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
public class BrandingDTO {

    private UUID id;
    private UUID distributorId;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String fontFamily;
    private String theme;
    private Boolean isDefault;
}
