package com.angellos.push.external;

import lombok.extern.slf4j.Slf4j;
import com.angellos.push.dto.ResponseRecord;
import com.angellos.push.config.ApplicationProperties;
import com.angellos.push.connector.KeycloakServiceConnectorClient;
import com.angellos.push.dto.KeycloakRoleDTO;
import com.angellos.push.dto.KeycloakUserDTO;
import com.angellos.push.utility.ObjectNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.angellos.push.utility.AppUtils.getResponseRecord;


@Slf4j
@Service
public record KeycloakService(ApplicationProperties properties,
                              KeycloakServiceConnectorClient keycloakServiceConnectorClient) {


    /**
     * This method searches for user existence based on a search string.
     *
     * @param userName The search string for user existence check.
     * @return The ID of the user if found.
     */
    public UUID findByUserName(final String userName) {
        return Objects.requireNonNull(keycloakServiceConnectorClient
                .searchForUserByUserName(properties.getKeycloak().getRealm(), userName).getBody()
        ).get(0).getId();
    }

    /**
     * This method searches for users based on a custom search parameter.
     *
     * @param search The custom search parameter.
     * @return A list of KeycloakUserDTOs matching the search criteria.
     */
    public List<KeycloakUserDTO> findBySearchParam(final String search){
        return Objects.requireNonNull(keycloakServiceConnectorClient
                        .searchForUserBySearchParam(properties.getKeycloak().getRealm(), search))
                .getBody();
    }

    /**
     * This method fetches available roles for a user from the Keycloak service.
     *
     * @param userId The id representing the user to be given a role.
     */
    public List<KeycloakRoleDTO> findAvailableRealmRoles(final UUID userId) {
        return Objects.requireNonNull(keycloakServiceConnectorClient
                .getAvailableUserRoles(properties.getKeycloak().getRealm(),
                        userId,0,101)).getBody();
    }

    /**
     * This method updates roles for a user in the Keycloak service.
     *
     * @param userId The id representing the user to be given a role.
     * @param keycloakRoles The DTO representing the role to be given to the user.
     */
    public ResponseEntity<Void> updateUserRole(final UUID userId, List<KeycloakRoleDTO> keycloakRoles) {
        return Objects.requireNonNull(keycloakServiceConnectorClient
                .updateUserRoles(properties.getKeycloak().getRealm(),
                        userId,keycloakRoles));
    }

    /**
     * This method deletes roles for a user in the Keycloak service.
     *
     * @param userId The id representing the user.
     * @param keycloakRoleDTO The DTO representing the role to be deleted for the user.
     */
    public ResponseEntity<Void> deleteUserRole(final UUID userId, List<KeycloakRoleDTO> keycloakRoleDTO) {
        return Objects.requireNonNull(keycloakServiceConnectorClient
                .deleteUserRoles(properties.getKeycloak().getRealm(),
                        userId,keycloakRoleDTO));
    }

    /**
     * This method saves a user to the Keycloak service.
     *
     * @param keycloakUserDTO The DTO representing the user to be saved.
     */
    public ResponseEntity<ResponseRecord> saveUser(final KeycloakUserDTO keycloakUserDTO) {
        ResponseRecord response;
        try {
            final ResponseEntity<KeycloakUserDTO> keycloakResponse =
                    keycloakServiceConnectorClient
                            .saveUser(properties.getKeycloak().getRealm(), keycloakUserDTO);
            log.info("keycloakResponse {}", keycloakResponse);
            response = getResponseRecord("Success", HttpStatus.valueOf(keycloakResponse.getStatusCode().value()), keycloakResponse.getBody());

        } catch (ResponseStatusException e){
            log.error("Exception Occurred!, statusCode -> {} and Message -> {}", e.getStatusCode(), e.getReason());
            response = getResponseRecord(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception occurred! Reason -> {}", message);
            response = getResponseRecord(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseRecord(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response.toResponseEntity();
    }

    /**
     * This method updates a user in the Keycloak service.
     *
     * @param id               The ID of the user to be updated.
     * @param keycloakUserDTO  The DTO containing updated user information.
     * @return A ResponseEntity containing the updated user DTO.
     */
    public ResponseEntity<ResponseRecord> updateUser(UUID id, KeycloakUserDTO keycloakUserDTO) {
        ResponseRecord response;
        try {

            List<KeycloakRoleDTO> assignedRoles = Objects.requireNonNull(keycloakServiceConnectorClient
                            .getAssignedUserRoles(properties.getKeycloak().getRealm(), id)
                            .getBody()).stream().filter(role -> !"default-roles-ceptra_op".equals(role.getName()))
                    .toList();

            keycloakServiceConnectorClient.deleteUserRoles(properties.getKeycloak().getRealm(),
                    id,assignedRoles);
            var record =
                    keycloakServiceConnectorClient.updateUser(id, properties.getKeycloak().getRealm(),
                            keycloakUserDTO).getBody();

            keycloakServiceConnectorClient.updateUserRoles(properties.getKeycloak().getRealm(),
                    id,keycloakUserDTO.getUserRoles());

            response = getResponseRecord("Success", HttpStatus.ACCEPTED, record);

        } catch (ResponseStatusException e){
            log.error("Exception Occurred!, statusCode -> {} and Message -> {}", e.getStatusCode(), e.getReason());
            response = getResponseRecord(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception occurred! Reason -> {}", message);
            response = getResponseRecord(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseRecord(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response.toResponseEntity();
    }

}
