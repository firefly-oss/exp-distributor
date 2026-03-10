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
public class TermsDTO {

    private UUID id;
    private UUID distributorId;
    private String title;
    private String content;
    private String version;
    private String status;
    private LocalDateTime effectiveDate;
    private LocalDateTime expirationDate;
    private LocalDateTime signedDate;
    private Boolean isActive;
}
