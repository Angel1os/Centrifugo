package com.angellos.push.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO implements Serializable {

    private UUID id;
    private String userName;
    @NotEmpty
    @Size(min = 5, message = "User email should be least 5 characters")
    @Email(message = "Invalid email address")
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<UUID> roles = new ArrayList<>();
    private List<String> userRoles = new ArrayList<>();
    private boolean enabled;
    private boolean exists;

}
