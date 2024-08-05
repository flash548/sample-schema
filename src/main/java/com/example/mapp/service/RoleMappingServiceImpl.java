package com.example.mapp.service;

import com.example.mapp.model.*;
import com.example.mapp.repository.FormRepository;
import com.example.mapp.repository.ProgramRepository;
import com.example.mapp.repository.RoleRepository;
import com.example.mapp.repository.SecurityFunctionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMappingServiceImpl implements RoleMappingService {

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FormRepository formRepository;

    @Autowired
    SecurityFunctionRepository securityFunctionRepository;

    private Program getProgramByName(String programName) {
        return programRepository.findByName(programName)
                .orElseThrow(() -> new RuntimeException("Pgm Name not found"));
    }

    @Override
    @Transactional
    public Program createProgram(String programName) {
        if (!programRepository.existsByNameIgnoreCase(programName)) {
            return programRepository.save(Program.builder().name(programName).build());
        } else {
            throw new RuntimeException("Program name already exists in OIS");
        }
    }

    @Override
    public Role createOISRole(String name) {
        return roleRepository.save(Role.builder().name(name.toUpperCase()).build());
    }

    @Override
    public Role updateOISRoleName(Long existingRoleId, String name) {
        Role r = roleRepository.findById(existingRoleId).orElseThrow(() -> new RuntimeException("Role id not found"));
        if (!roleRepository.existsByNameIgnoreCase(name)) {
            r.setName(name);
            return roleRepository.save(r);
        } else {
            throw new RuntimeException("That role already exists in OIS");
        }
    }

    @Override
    @Transactional
    public Program getProgramById(Long id) {
        return programRepository.findById(id).orElseThrow(() -> new RuntimeException("Pgm Id not found"));
    }

    @Override
    public Program associateRoleToProgram(String programName, String roleName, List<String> functionNames) {

        // make sure the program has the security functions defined
        Program p = this.addSecurityFunctionsToProgram(programName, functionNames);

        // pull the role and functionNames (create if not present - a good/bad idea?)
        Role r = roleRepository.findByNameIgnoreCase(roleName).orElseGet(() -> roleRepository.save(Role.builder()
                .name(roleName)
                .build()));

        // get the program's security functions we're concerned with
        List<SecurityFunction> funcs = p.getSecurityFunctions()
                .stream()
                .filter(f -> functionNames.stream().map(n -> n.toUpperCase()).toList().contains(f.getName())).toList();

        // map the role to the program with said security functions
        p.getRoleFunctionMappings().removeIf(item -> item.getRole().equals(r) && item.getProgram().equals(p));
        funcs.forEach(f -> p.getRoleFunctionMappings().add(RoleFunctionMapping.builder()
                .id(new RoleFunctionMapping.RoleFunctionMappingId(p.getId(), r.getId(), f.getId()))
                .role(r)
                .program(p)
                .securityFunction(f)
                .build()));

        return programRepository.save(p);
    }

    @Override
    public Program addSecurityFunctionsToProgram(String programName, List<String> functionNames) {
        Program p = this.getProgramByName(programName);

        p.getSecurityFunctions().addAll(functionNames.stream().map(name -> SecurityFunction.builder()
                .name(name)
                .programId(p.getId())
                .build()).toList());

        return programRepository.save(p);
    }

    @Override
    @Transactional
    public Program addFormToProgram(String programName, String formName) {
        Program p = this.getProgramByName(programName);
        p.getForms().add(Form.builder().name(formName).build());
        return programRepository.save(p);
    }

    @Override
    public Program associateRoleToForm(String programName,
                                       String formName,
                                       String roleName,
                                       List<String> functionNames) {
        // make sure the program has the security functions defined
        Program p = this.addSecurityFunctionsToProgram(programName, functionNames);

        Form theForm = p.getForms()
                .stream()
                .filter(form -> form.getName().equals(formName.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Form not found"));

        // pull the role and functionNames (create if not present - a good/bad idea?)
        Role r = roleRepository.findByNameIgnoreCase(roleName).orElseGet(() -> roleRepository.save(Role.builder()
                .name(roleName)
                .build()));

        // get the program's security functions we're concerned with
        List<SecurityFunction> funcs = p.getSecurityFunctions()
                .stream()
                .filter(func -> functionNames.stream().map(n -> n.toUpperCase()).toList().contains(func.getName())).toList();

        // map the role to the form with said security functions
        theForm.getRoleFunctionFormMappings().removeIf(item -> item.getRole().equals(r) && item.getForm().equals(theForm));
        funcs.forEach(func -> theForm.getRoleFunctionFormMappings().add(RoleFunctionFormMapping.builder()
                .id(new RoleFunctionFormMapping.RoleFunctionFormMappingId(p.getId(), r.getId(), func.getId(), theForm.getId()))
                .role(r)
                .program(p)
                .securityFunction(func)
                .form(theForm)
                .build()));

        return programRepository.save(p);
    }
}
