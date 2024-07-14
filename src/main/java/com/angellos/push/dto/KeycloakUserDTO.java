package com.angellos.push.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class KeycloakUserDTO {

    private UUID id;
    @NotEmpty
    @JsonProperty("username")
    private String userName;
    private String firstName;
    private String lastName;
    @NotEmpty
    private String email;
    private boolean emailVerified;
    private Boolean enabled = true;
    private Map<String, List> attributes = new HashMap<>();
    private List<KeycloakCredentialsDTO> credentials = new ArrayList<>();
    @JsonIgnore
    private List<KeycloakRoleDTO> userRoles = new ArrayList<>();

}
