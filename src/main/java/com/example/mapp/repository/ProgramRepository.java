package com.example.mapp.repository;

import com.example.mapp.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<Program> findByName(String name);

}
