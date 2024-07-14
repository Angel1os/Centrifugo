package com.angellos.push.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class KeycloakCredentialsDTO {

    private Boolean temporary;
    private String type;
    private String value;

}
