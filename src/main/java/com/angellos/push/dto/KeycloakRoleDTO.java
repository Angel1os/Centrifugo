package com.angellos.push.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class KeycloakRoleDTO {

    private UUID id;
    private String name;
    private String description;
    private Boolean composite;
    private Boolean clientRole;
    private UUID containerId;

}
