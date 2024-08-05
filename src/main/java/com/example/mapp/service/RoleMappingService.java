package com.example.mapp.service;

import com.example.mapp.model.Program;
import com.example.mapp.model.Role;

import java.util.List;

public interface RoleMappingService {

    Role createOISRole(String name);
    Role updateOISRoleName(Long existingRoleId, String name);

    Program createProgram(String programName);
    Program getProgramById(Long id);
    Program associateRoleToProgram(String programName, String roleName, List<String> functionNames);
    Program addSecurityFunctionsToProgram(String programName, List<String> functionNames);

    Program addFormToProgram(String programName, String formName);
    Program associateRoleToForm(String programName, String formName, String roleName, List<String> functionNames);
}
