package com.example.mapp.repository;

import com.example.mapp.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<Program> findByName(String name);

    /**
     * A very costly query **that should probably be refactored** given its eagerness...
     * @param roleName rolename (and its permissions) to filter out of all the programs
     * @return programs that with security functions mapped only to given rolename
     */
    @Query("""
                select p from Program p 
                    JOIN FETCH p.roleFunctionMappings rfm 
                    JOIN FETCH p.forms f 
                    JOIN FETCH f.roleFunctionFormMappings rffm 
                    JOIN FETCH rfm.role r 
                    JOIN FETCH rffm.role r2
                    WHERE r.name = :roleName and r2.name = :roleName
            """)
    List<Program> getAllProgramsContainingRoleName(String roleName);
}
