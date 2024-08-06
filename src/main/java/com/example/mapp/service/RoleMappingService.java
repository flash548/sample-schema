package com.example.mapp.service;

import com.example.mapp.dto.FormDto;
import com.example.mapp.dto.ProgramDto;
import com.example.mapp.dto.RoleWithProgramsDto;
import com.example.mapp.dto.SecurityFunctionDto;
import com.example.mapp.model.Form;
import com.example.mapp.model.Program;
import com.example.mapp.model.Role;
import com.example.mapp.model.SecurityFunction;

import java.util.List;

public interface RoleMappingService {

    Role createRoleName(String name);
    Role updateRoleName(Long existingRoleId, String name);
    List<Role> getRoles();

    Program createProgram(String programName);
    List<Program> getPrograms();
    Program getProgramById(Long id);
    Program associateRoleToProgram(String programName, String roleName, List<String> functionNames);
    Program addSecurityFunctionsToProgram(String programName, List<String> functionNames);

    Program addFormToProgram(String programName, String formName);
    Program associateRoleToForm(String programName, String formName, String roleName, List<String> functionNames);

    RoleWithProgramsDto mapRoleAndProgramsToDto(String roleName);
    ProgramDto mapProgramToDto(Program program);
    FormDto mapFormToDto(Form form);
    SecurityFunctionDto mapSecurityFunctionToDto(SecurityFunction securityFunction);

}
