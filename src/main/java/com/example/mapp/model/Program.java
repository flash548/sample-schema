package com.example.mapp.model;

import com.example.mapp.model.util.UppercasedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Optional;
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
public class Program implements UppercasedEntity {

    @Id
    @GeneratedValue
    Long id;

    @NotNull String name;

    /**
     * Forms underneath this program
     */
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    Set<Form> forms = new HashSet<>();

    /**
     * Roles and their associated SecurityFunction with this program
     */
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    Set<RoleFunctionMapping> roleFunctionMappings = new HashSet<>();

    /**
     * This program's defined security functions
     */
    @Builder.Default
    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    Set<SecurityFunction> securityFunctions = new HashSet<>();

    @PreUpdate
    @PrePersist
    @Override
    public void uppercaseName() {
        this.name = name.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Program program = (Program) o;
        return name.toUpperCase().equals(program.name.toUpperCase());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public Optional<Form> getFormNamed(String formName) {
        return this.getForms().stream().filter(f -> f.getName().equals(formName.toUpperCase())).findFirst();
    }
}
