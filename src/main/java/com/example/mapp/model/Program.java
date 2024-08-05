package com.example.mapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * A "program" can own forms below it and have its own set of security functions defined for it
 * A program also can have Roles mapped to it
 */
@Entity
@Table(name = "programs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Program {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    String name;

    /**
     * Forms underneath this program
     */
    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    Set<Form> forms = new HashSet<>();

    /**
     * Roles and their associated SecurityFunction with this program
     */
    @Builder.Default
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    Set<RoleFunctionMapping> roleFunctionMappings = new HashSet<>();

    /**
     * This program's defined security functions
     */
    @Builder.Default
    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    Set<SecurityFunction> securityFunctions = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Program program = (Program) o;

        return name.equals(program.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
