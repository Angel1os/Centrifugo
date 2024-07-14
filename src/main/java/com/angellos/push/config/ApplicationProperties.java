package com.angellos.push.config;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("push")
@Data
public class ApplicationProperties {

    /**
     * Configuration properties for Keycloak.
     */
    @NestedConfigurationProperty
    private final @Valid Keycloak keycloak = new Keycloak();

    /**
     * This class represents the Keycloak configuration properties.
     */
    @Data
    public static class Keycloak {
        private String authServerUrl;
        private String realm;
        private String clientId;
        private String clientSecret;
        private String tokenEndpoint;
    }
}
