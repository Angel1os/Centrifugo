package com.angellos.push.model;

import com.angellos.push.dto.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "user"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "username",nullable = false, length = 20)
    private String userName;

    @Column(name = "email",nullable = false, length = 65)
    private String email;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            schema = "ceptra_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleModel> roles = new HashSet<>();


    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "created_by",nullable = false)
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private ZonedDateTime createdAt;

    /**
     * This method sets 'isEnabled' to true before persisting the user model.
     * It is executed before the model is saved to the database.
     */
    @PrePersist
    public void prePersist() {
        this.isEnabled = true;
    }

    public UserDTO toDTO() {
        return UserDTO
                .builder()
                .id(this.id)
                .userName(this.userName)
                .email(this.email)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .phoneNumber(this.phoneNumber)
                .userRoles(this.roles.stream().map(RoleModel::getName).collect(Collectors.toList()))
                .enabled(this.isEnabled)
                .build();
    }
}
