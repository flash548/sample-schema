package com.example.mapp.model;

import com.example.mapp.model.util.UppercasedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


/**
 * A Child resource of a Program
 */
@Entity
@Table(name = "forms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Form implements UppercasedEntity {

    @Id
    @GeneratedValue
    Long id;

    @NotNull String name;

    @ManyToOne(fetch = FetchType.LAZY)
    Program owner;

    /**
     * Roles and their associated SecurityFunction with this form
     */
    @Builder.Default
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    Set<RoleFunctionFormMapping> roleFunctionFormMappings = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Form form = (Form) o;

        return name.toUpperCase().equals(form.name.toUpperCase());
    }

    @PreUpdate
    @PrePersist
    @Override
    public void uppercaseName() {
        this.name = name.toUpperCase();
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
