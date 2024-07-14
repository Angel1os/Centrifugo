package com.angellos.push.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.angellos.push.dto.RoleDTO;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "role"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleModel {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name",nullable = false, length = 65)
    private String name;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Column(name = "created_by",nullable = false)
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();


    public RoleDTO toDTO() {
        return RoleDTO
                .builder()
                .id(this.id)
                .name(this.name)
                .enabled(this.isEnabled)
                .build();
    }
}
