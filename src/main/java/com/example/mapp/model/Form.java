package com.example.mapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


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
public class Form {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    Program owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Form form = (Form) o;

        return name.equals(form.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
