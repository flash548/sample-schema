package com.example.mapp.model;

import com.example.mapp.model.util.UppercasedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Security function is basically a "permission" within a Program
 */
@Entity
@Table(name = "security_functions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SecurityFunction implements UppercasedEntity {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    String name;

    @NotNull
    @Column(name = "program_id")
    @ToString.Exclude
    Long programId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", insertable = false, updatable = false)
    Program program;

    @PreUpdate
    @PrePersist
    @Override
    public void uppercaseName() {
        this.name = this.name.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecurityFunction that = (SecurityFunction) o;

        if (!name.equals(that.name)) return false;
        return programId.equals(that.programId);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + programId.hashCode();
        return result;
    }
}
