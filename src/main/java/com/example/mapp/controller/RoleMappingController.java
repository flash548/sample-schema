package com.example.mapp.controller;

import com.example.mapp.dto.ProgramDto;
import com.example.mapp.dto.RoleWithProgramsDto;
import com.example.mapp.model.Role;
import com.example.mapp.service.RoleMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RoleMappingController {

    @Autowired
    RoleMappingService roleMappingService;

    @GetMapping("/roleNames")
    public ResponseEntity<List<Role>> getRoles() {
        return new ResponseEntity<>(roleMappingService.getRoles(), HttpStatus.OK);
    }

    @GetMapping("/rolesDetails")
    public ResponseEntity<List<RoleWithProgramsDto>> getRoleDetails(@RequestParam(required = false) String roleName) {
        List<RoleWithProgramsDto> retVal = new ArrayList<>();
        if (roleName == null) {
            List<Role> allRoles = roleMappingService.getRoles();
            retVal.addAll(allRoles.stream().map(r -> roleMappingService.mapRoleAndProgramsToDto(r.getName())).toList());
        } else {
            retVal.add(roleMappingService.mapRoleAndProgramsToDto(roleName));
        }

        return new ResponseEntity<>(retVal, HttpStatus.OK);
    }

    @GetMapping("/programs")
    public ResponseEntity<List<ProgramDto>> getPrograms() {
        roleMappingService.mapRoleAndProgramsToDto("ROLE1");
        return new ResponseEntity<>(roleMappingService.getPrograms()
                .stream()
                .map(f -> roleMappingService.mapProgramToDto(f))
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/programs/{id}")
    public ResponseEntity<ProgramDto> getProgram(@PathVariable Long id) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.getProgramById(id)), HttpStatus.OK);
    }

    @PostMapping("/add-program")
    public ResponseEntity<ProgramDto> postProgram(@RequestParam String programName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.createProgram(programName)),
                HttpStatus.OK);
    }

    @PostMapping("/add-form")
    public ResponseEntity<ProgramDto> postForm(@RequestParam String programName, @RequestParam String formName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.addFormToProgram(programName,
                formName)),
                HttpStatus.OK);
    }


    @PostMapping("/roles/add-role")
    public ResponseEntity<Role> postRole(@RequestParam String roleName) {
        return new ResponseEntity<>(roleMappingService.createRoleName(roleName), HttpStatus.OK);
    }

    @PostMapping(value = "/map-role-to-form")
    public ResponseEntity<ProgramDto> mapRoleToForm(@RequestParam String programName,
                                                    @RequestParam String formName,
                                                    @RequestParam String roleName,
                                                    @RequestBody List<String> securityFunctions) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.associateRoleToForm(
                programName,
                formName,
                roleName,
                securityFunctions)),
                HttpStatus.OK);
    }

    @PostMapping(value = "/map-role-to-program")
    public ResponseEntity<ProgramDto> mapRoleToProgram(@RequestParam String programName,
                                                       @RequestParam String roleName,
                                                       @RequestBody List<String> securityFunctions) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.associateRoleToProgram(
                programName,
                roleName,
                securityFunctions)),
                HttpStatus.OK);
    }

}
