package com.angellos.push.config;

import lombok.RequiredArgsConstructor;
import com.angellos.push.connector.KeycloakServiceConnectorClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {

    private static final String KEYCLOAK_CLIENT_REGISTRATION_ID = "keycloak";
    private final ApplicationProperties applicationProperties;
    private final OAuth2AuthorizedClientService clientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Configures the manager for authorized clients.
     *
     * @return The authorized client manager.
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(){
        final OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder
                        .builder()
                        .refreshToken()
                        .clientCredentials()
                        .build();

        final AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    /**
     * Configures a WebClient with OAuth 2.0 authentication capabilities for Keycloak.
     *
     * @param authorizedClientManager The authorized client manager.
     * @return The configured WebClient for Keycloak.
     */
    @Bean
    WebClient webClientForKeycloak(final OAuth2AuthorizedClientManager authorizedClientManager){
        final ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId(KEYCLOAK_CLIENT_REGISTRATION_ID);
        return WebClient
                .builder()
                .baseUrl(applicationProperties.getKeycloak().getAuthServerUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunction.ofResponseProcessor(this::handleErrorResponse))
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }

    /**
     * Creates a client for the Keycloak service connector.
     *
     * @return The Keycloak service connector client.
     */
    @Bean
    KeycloakServiceConnectorClient keycloakServiceConnectorClient(WebClient webClientForKeycloak) {
        final HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClientForKeycloak))
                .build();
        return httpServiceProxyFactory.createClient(KeycloakServiceConnectorClient.class);
    }

    /**
     * Handles error responses from external servers.
     *
     * @param response The client response.
     * @return A mono of the client response.
     */
    private Mono<ClientResponse> handleErrorResponse(final ClientResponse response){
        if (response.statusCode().isError()){
            final Mono<String> clientExceptionMono = response.bodyToMono(String.class);
            return clientExceptionMono.flatMap(exception ->
                    Mono.error(new ResponseStatusException(response.statusCode(), exception)));
        }
        return Mono.just(response);
    }
}
