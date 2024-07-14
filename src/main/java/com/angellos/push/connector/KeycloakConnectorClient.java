package com.angellos.push.connector;

import com.angellos.push.dto.KeycloakRoleDTO;
import com.angellos.push.dto.KeycloakUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.UUID;

public interface KeycloakConnectorClient {

    @GetExchange("/admin/realms/{realm}/users?exact=true")
    ResponseEntity<List<KeycloakUserDTO>> searchForUserByUserName(@PathVariable final String realm,
                                                                  @RequestParam(name = "username")
                                                                  final String username);

    @GetExchange("/admin/realms/{realm}/users")
    ResponseEntity<List<KeycloakUserDTO>> searchForUserBySearchParam(@PathVariable final String realm,
                                                                     @RequestParam String search);

    @PostExchange("admin/realms/{realm}/users")
    ResponseEntity<KeycloakUserDTO> saveUser(@PathVariable final String realm,
                                             @RequestBody final KeycloakUserDTO keycloakUserDTO);

    @PutExchange("/admin/realms/{realm}/users/{id}")
    ResponseEntity<KeycloakUserDTO> updateUser(@PathVariable UUID id, @PathVariable final String realm,
                                               @RequestBody final KeycloakUserDTO keycloakUserDTO);

    @GetExchange("/admin/realms/{realm}/users/{userId}/role-mappings/realm/available")
    ResponseEntity<List<KeycloakRoleDTO>> getAvailableUserRoles(@PathVariable final String realm,
                                                                @PathVariable UUID userId,
                                                                @RequestParam int first,
                                                                @RequestParam int max);

    @GetExchange("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    ResponseEntity<List<KeycloakRoleDTO>> getAssignedUserRoles(@PathVariable final String realm,
                                                               @PathVariable UUID userId);

    @PostExchange("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    ResponseEntity<Void> updateUserRoles(@PathVariable String realm,
                                         @PathVariable UUID userId,
                                         @RequestBody List<KeycloakRoleDTO> roles);

    @DeleteExchange("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    ResponseEntity<Void> deleteUserRoles(@PathVariable String realm,
                                         @PathVariable UUID userId,
                                         @RequestBody List<KeycloakRoleDTO> roles);

}
