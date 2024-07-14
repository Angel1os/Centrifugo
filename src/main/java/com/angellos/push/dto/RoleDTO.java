package com.angellos.push.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO {

    private UUID id;
    @NotNull
    @NotEmpty
    @Size(min = 3,max = 60)
    private String name;
    @Size(min = 3,max = 255)
    private String description;
    @JsonProperty("isEnabled")
    private boolean enabled;

}
