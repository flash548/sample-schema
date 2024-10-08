package com.example.mapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity/Table that maps Roles to Programs to SecurityFunctions
 */
@Entity
@Table(name = "role_function_mappings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoleFunctionMapping {

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RoleFunctionMappingId {

        @Column(name = "program_id")
        Long programId;

        @Column(name = "role_id")
        Long roleId;

        @Column(name = "security_function_id")
        Long securityFunctionId;

    }

    @Id
    RoleFunctionMappingId id = new RoleFunctionMappingId();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "program_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    Program program;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    Role role;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "security_function_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    SecurityFunction securityFunction;

}
