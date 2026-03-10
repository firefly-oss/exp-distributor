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
public class AgentDTO {

    private UUID id;
    private UUID distributorId;
    private String firstName;
    private String lastName;
    private String email;
    private String employeeCode;
    private Boolean isActive;
}
