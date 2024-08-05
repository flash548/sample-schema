package com.example.mapp.model;

import com.example.mapp.model.util.UppercasedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Top level role defined that would need its analog in Keycloak defined
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role implements UppercasedEntity {

    @Id
    @GeneratedValue
    Long id;

    @NotNull String name;

    @PrePersist
    @PreUpdate
    @Override
    public void uppercaseName() {
        this.name = this.name.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
