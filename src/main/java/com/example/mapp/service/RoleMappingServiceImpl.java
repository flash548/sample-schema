package com.example.mapp.service;

import com.example.mapp.dto.FormDto;
import com.example.mapp.dto.ProgramDto;
import com.example.mapp.dto.RoleWithProgramsDto;
import com.example.mapp.dto.SecurityFunctionDto;
import com.example.mapp.exception.ConflictException;
import com.example.mapp.exception.NotFoundException;
import com.example.mapp.model.*;
import com.example.mapp.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleMappingServiceImpl implements RoleMappingService {

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FormRepository formRepository;

    @Autowired
    RoleFunctionMappingRepository roleFunctionMappingRepository;

    @Autowired
    RoleFunctionFormMappingRepository roleFunctionFormMappingRepository;

    @Autowired
    SecurityFunctionRepository securityFunctionRepository;

    @Autowired
    EntityManager entityManager;

    private Program getProgramByName(String programName) {
        return programRepository.findByName(programName).orElseThrow(() -> new NotFoundException("Pgm Name not found"));
    }

    @Override
    @Transactional
    public Program createProgram(String programName) {
        if (!programRepository.existsByNameIgnoreCase(programName)) {
            return programRepository.save(Program.builder().name(programName).build());
        } else {
            throw new ConflictException("Program name already exists");
        }
    }

    @Override
    public List<Program> getPrograms() {
        return programRepository.findAll();
    }

    @Override
    public Role createRoleName(String name) {
        return roleRepository.save(Role.builder().name(name.toUpperCase()).build());
    }

    @Override
    public Role updateRoleName(Long existingRoleId, String name) {
        Role r = roleRepository.findById(existingRoleId).orElseThrow(() -> new NotFoundException("Role id not found"));
        if (!roleRepository.existsByNameIgnoreCase(name)) {
            r.setName(name);
            return roleRepository.save(r);
        } else {
            throw new ConflictException("That role already exists");
        }
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Program getProgramById(Long id) {
        return programRepository.findById(id).orElseThrow(() -> new NotFoundException("Pgm Id not found"));
    }

    @Override
    public Program associateRoleToProgram(String programName, String roleName, List<String> functionNames) {

        // make sure the program has the security functions defined
        Program p = this.addSecurityFunctionsToProgram(programName, functionNames);

        // pull the role and functionNames (create if not present - a good/bad idea?)
        Role r = roleRepository.findByNameIgnoreCase(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

        // get the program's security functions we're concerned with
        List<SecurityFunction> funcs = p.getSecurityFunctions()
                .stream()
                .filter(f -> functionNames.stream().map(String::toUpperCase).toList().contains(f.getName()))
                .toList();

        // map the role to the program with said security functions
        p.getRoleFunctionMappings().removeIf(item -> item.getRole().equals(r) && item.getProgram().equals(p));
        funcs.forEach(f -> p.getRoleFunctionMappings()
                .add(RoleFunctionMapping.builder()
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

        p.getSecurityFunctions()
                .addAll(functionNames.stream()
                        .map(name -> SecurityFunction.builder().name(name).programId(p.getId()).build())
                        .toList());

        return programRepository.save(p);
    }

    @Override
    @Transactional
    public Program addFormToProgram(String programName, String formName) {
        Program p = this.getProgramByName(programName);
        p.getForms().add(Form.builder().name(formName).owner(p).build());
        return programRepository.save(p);
    }

    @Override
    public Program associateRoleToForm(String programName,
                                       String formName,
                                       String roleName,
                                       List<String> functionNames) {
        // make sure the program has the security functions defined
        Program p = this.addSecurityFunctionsToProgram(programName, functionNames);

        // create FORM id on the fly?  good or bad idea??
        Form theForm = p.getFormNamed(formName)
                .orElseGet(() -> (this.addFormToProgram(programName, formName).getFormNamed(formName)).orElseThrow());

        // pull the role and functionNames (create if not present - a good/bad idea?)
        Role r = roleRepository.findByNameIgnoreCase(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

        // get the program's security functions we're concerned with
        List<SecurityFunction> funcs = p.getSecurityFunctions()
                .stream()
                .filter(func -> functionNames.stream().map(String::toUpperCase).toList().contains(func.getName()))
                .toList();

        // map the role to the form with said security functions
        theForm.getRoleFunctionFormMappings()
                .removeIf(item -> item.getRole().equals(r) && item.getForm().equals(theForm));
        funcs.forEach(func -> theForm.getRoleFunctionFormMappings()
                .add(RoleFunctionFormMapping.builder()
                        .id(new RoleFunctionFormMapping.RoleFunctionFormMappingId(p.getId(),
                                r.getId(),
                                func.getId(),
                                theForm.getId()))
                        .role(r)
                        .program(p)
                        .securityFunction(func)
                        .form(theForm)
                        .build()));

        return programRepository.save(p);
    }

    @Override
    public RoleWithProgramsDto mapRoleAndProgramsToDto(String roleName) {
        List<Program> allPrograms = programRepository.findAll();
        List<Program> returnList = new ArrayList<>();
        Role r = roleRepository.findByNameIgnoreCase(roleName).orElseThrow();

        // go through all programs and filter out other roles besides roleName
        for (Program p : allPrograms) {
            Set<Form> forms = p.getForms();
            Set<Form> filteredForms = new HashSet<>();
            Set<RoleFunctionMapping> roleList = new HashSet<>(
                    roleFunctionMappingRepository.findAllByProgramAndRole(p, r));

            for (Form f : forms) {
                var result = roleFunctionFormMappingRepository.findAllByFormAndRoleAndProgram(f, r, p);
                if (!result.isEmpty()) {
                    filteredForms.add(Form.builder()
                            .name(f.getName())
                            .id(f.getId())
                            .owner(p)
                            .roleFunctionFormMappings(new HashSet<>(result))
                            .build());
                }
            }

            // if this program has some association to given role, include it in the return
            if (!filteredForms.isEmpty() || !roleList.isEmpty())
                returnList.add(Program.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .forms(filteredForms)
                        .roleFunctionMappings(roleList)
                        .build());
        }


        return RoleWithProgramsDto.builder()
                .roleName(roleName.toUpperCase())
                .programs(returnList.stream().map(this::mapProgramToDto).toList())
                .build();

    }

    @Override
    public ProgramDto mapProgramToDto(Program program) {
        Map<String, List<String>> groupedRoles = reduceProgramRoleMappingsToMap(program);
        return ProgramDto.builder()
                .id(program.getId())
                .name(program.getName())
                .forms(program.getForms().stream().map(this::mapFormToDto).toList())
                .roleMappings(groupedRoles)
                .build();
    }

    private static Map<String, List<String>> reduceProgramRoleMappingsToMap(Program program) {
        Map<String, List<String>> groupedRoles = new HashMap<>();
        program.getRoleFunctionMappings().stream().forEach(item -> {
            if (!groupedRoles.containsKey(item.getRole().getName())) {
                groupedRoles.put(item.getRole().getName(), new ArrayList<>());
            }
            groupedRoles.get(item.getRole().getName()).add(item.getSecurityFunction().getName());
        });
        return groupedRoles;
    }

    @Override
    public FormDto mapFormToDto(Form form) {
        Map<String, List<String>> groupedRoles = reduceFormRoleMappingsToMap(form);
        return FormDto.builder().id(form.getId()).name(form.getName()).roleMappings(groupedRoles).build();
    }

    private static Map<String, List<String>> reduceFormRoleMappingsToMap(Form form) {
        Map<String, List<String>> groupedRoles = new HashMap<>();
        form.getRoleFunctionFormMappings().stream().forEach(item -> {
            if (!groupedRoles.containsKey(item.getRole().getName())) {
                groupedRoles.put(item.getRole().getName(), new ArrayList<>());
            }
            groupedRoles.get(item.getRole().getName()).add(item.getSecurityFunction().getName());
        });
        return groupedRoles;
    }

    @Override
    public SecurityFunctionDto mapSecurityFunctionToDto(SecurityFunction securityFunction) {
        return SecurityFunctionDto.builder().name(securityFunction.getName()).build();
    }
}
