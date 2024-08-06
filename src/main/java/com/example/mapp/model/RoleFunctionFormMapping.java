package com.example.mapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity/Table that maps Roles to a Program's owned Forms to SecurityFunctions
 * Since forms can override a parent's role to program mapping (then this join table is needed)
 */
@Entity
@Table(name = "role_function_form_mappings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoleFunctionFormMapping {

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RoleFunctionFormMappingId {

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
    RoleFunctionFormMappingId id = new RoleFunctionFormMappingId();

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
