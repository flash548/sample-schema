package com.example.mapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity/Table that maps roles to programs to security functions (and possibly to forms)
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

        @Column(name = "form_id")
        Long formId;

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

    @ManyToOne
    @JoinColumn(name = "form_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    Form form;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "security_function_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    SecurityFunction securityFunction;

}
