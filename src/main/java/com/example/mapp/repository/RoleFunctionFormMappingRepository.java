package com.example.mapp.repository;

import com.example.mapp.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleFunctionFormMappingRepository
        extends JpaRepository<RoleFunctionFormMapping, RoleFunctionMapping.RoleFunctionMappingId> {

    List<RoleFunctionFormMapping> findAllByFormAndRoleAndProgram(Form f, Role r, Program p);
}
