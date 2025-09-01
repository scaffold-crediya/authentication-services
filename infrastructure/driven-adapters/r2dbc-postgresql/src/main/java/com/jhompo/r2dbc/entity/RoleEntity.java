package com.jhompo.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleEntity {

    @Id
    private UUID id;  // Cambiar String → UUID

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("active")
    private Boolean active;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("created_by")
    private String createdBy;

    @Column("updated_by")
    private String updatedBy;

}
