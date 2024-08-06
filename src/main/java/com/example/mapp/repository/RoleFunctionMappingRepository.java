package com.example.mapp.repository;

import com.example.mapp.model.Program;
import com.example.mapp.model.Role;
import com.example.mapp.model.RoleFunctionMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleFunctionMappingRepository
        extends JpaRepository<RoleFunctionMapping, RoleFunctionMapping.RoleFunctionMappingId> {

    List<RoleFunctionMapping> findAllByProgramAndRole(Program program, Role role);
}
