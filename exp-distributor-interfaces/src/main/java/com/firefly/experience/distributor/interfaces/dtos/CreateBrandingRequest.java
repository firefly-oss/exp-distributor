package com.firefly.experience.distributor.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBrandingRequest {

    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String fontFamily;
    private String theme;
}
