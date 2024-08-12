package com.example.mapp.controller;

import com.example.mapp.dto.*;
import com.example.mapp.model.Role;
import com.example.mapp.service.RoleMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Gets all role names")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoleListDto.class)))})
    @GetMapping("/roleNames")
    public ResponseEntity<RoleListDto> getRoles() {
        return new ResponseEntity<>(RoleListDto.builder().roles(roleMappingService.getRoles()).build(), HttpStatus.OK);
    }

    @Operation(summary = "Gets all roles with their associated programs/functions")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RoleWithProgramsListDto.class)))})
    @GetMapping("/rolesDetails")
    public ResponseEntity<RoleWithProgramsListDto> getRoleDetails(@RequestParam(required = false) String roleName) {
        List<RoleWithProgramsDto> retVal = new ArrayList<>();
        if (roleName == null) {
            List<Role> allRoles = roleMappingService.getRoles();
            retVal.addAll(allRoles.stream().map(r -> roleMappingService.mapRoleAndProgramsToDto(r.getName())).toList());
        } else {
            retVal.add(roleMappingService.mapRoleAndProgramsToDto(roleName));
        }

        return new ResponseEntity<>(RoleWithProgramsListDto.builder().roles(retVal).build(), HttpStatus.OK);
    }

    @Operation(summary = "Gets all programs")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramListDto.class)))})
    @GetMapping("/programs")
    public ResponseEntity<ProgramListDto> getPrograms() {
        roleMappingService.mapRoleAndProgramsToDto("ROLE1");
        return new ResponseEntity<>(ProgramListDto.builder()
                .programs(roleMappingService.getPrograms()
                        .stream()
                        .map(f -> roleMappingService.mapProgramToDto(f))
                        .toList())
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "Gets a program by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @GetMapping("/programs/{id}")
    public ResponseEntity<ProgramDto> getProgram(@PathVariable Long id) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.getProgramById(id)),
                HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of strings representing the security functions for a program",
            description = "Gets a list of collated permissions for a given program (and form if given).  Request body is a list of 0 or more roles for which to collate")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SecurityFunctionList.class)))})
    @PostMapping("/permissions-for-program")
    public ResponseEntity<SecurityFunctionList> getSecurityFunctionsForResource(@RequestParam String programName,
                                                                                @RequestParam(required = false) String formName,
                                                                                @RequestBody List<String> roleNames) {

        return new ResponseEntity<>(SecurityFunctionList.builder()
                .securityFunctions(roleMappingService.collateRolesToProgramAndForm(roleNames, programName, formName))
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "Add a new program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class))), @ApiResponse(responseCode = "409", description = "Program already exists", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping("/add-program")
    public ResponseEntity<ProgramDto> postProgram(@RequestParam String programName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.createProgram(programName)),
                HttpStatus.OK);
    }

    @Operation(summary = "Adds a new form underneath an existing program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping("/add-form")
    public ResponseEntity<ProgramDto> postForm(@RequestParam String programName, @RequestParam String formName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.addFormToProgram(programName,
                formName)), HttpStatus.OK);
    }

    @Operation(summary = "Removes a form underneath an existing program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping("/remove-form")
    public ResponseEntity<ProgramDto> removeForm(@RequestParam String programName, @RequestParam String formName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.removeFormFromProgram(
                programName,
                formName)), HttpStatus.OK);
    }

    @Operation(summary = "Removes a security function underneath an existing program", description = "Removes the security function as well from all usages within program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping("/remove-security-function")
    public ResponseEntity<ProgramDto> removeSecurityFunction(@RequestParam String programName,
                                                             @RequestParam String securityFunctionName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.removeSecurityFunctionFromProgram(
                programName,
                securityFunctionName)), HttpStatus.OK);
    }


    @Operation(summary = "Adds a new role to the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Role.class)))})
    @PostMapping("/roles/add-role")
    public ResponseEntity<Role> postRole(@RequestParam String roleName) {
        return new ResponseEntity<>(roleMappingService.createRoleName(roleName), HttpStatus.OK);
    }

    @Operation(summary = "Adds a new security function to an existing program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping("/add-security-functions")
    public ResponseEntity<ProgramDto> postSecurityFunctions(@RequestParam String programName,
                                                            @RequestBody List<String> functionNames) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.addSecurityFunctionsToProgram(
                programName,
                functionNames)), HttpStatus.OK);
    }

    @Operation(summary = "Maps a role to form within a program with given security functions of that program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping(value = "/map-role-to-form")
    public ResponseEntity<ProgramDto> mapRoleToForm(@RequestParam String programName,
                                                    @RequestParam String formName,
                                                    @RequestParam String roleName,
                                                    @RequestBody List<String> securityFunctions) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.associateRoleToForm(
                programName,
                formName,
                roleName,
                securityFunctions)), HttpStatus.OK);
    }

    @Operation(summary = "Maps a role to an existing program with given security functions of that program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping(value = "/map-role-to-program")
    public ResponseEntity<ProgramDto> mapRoleToProgram(@RequestParam String programName,
                                                       @RequestParam String roleName,
                                                       @RequestBody List<String> securityFunctions) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.associateRoleToProgram(
                programName,
                roleName,
                securityFunctions)), HttpStatus.OK);
    }

    @Operation(summary = "Removes a role from an existing program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping(value = "/remove-role-from-program")
    public ResponseEntity<ProgramDto> removeRoleFromProgram(@RequestParam String programName,
                                                            @RequestParam String roleName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.removeRoleFromProgram(
                programName,
                roleName)), HttpStatus.OK);
    }

    @Operation(summary = "Removes a role from an existing form within an existing program")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProgramDto.class)))})
    @PostMapping(value = "/remove-role-from-form")
    public ResponseEntity<ProgramDto> removeRoleFromProgramForm(@RequestParam String programName,
                                                                @RequestParam String formName,
                                                                @RequestParam String roleName) {
        return new ResponseEntity<>(roleMappingService.mapProgramToDto(roleMappingService.removeRoleFromProgramForm(
                programName,
                formName,
                roleName)), HttpStatus.OK);
    }

    @Operation(summary = "Hard deletes a program (and all its forms/security functions)")
    @ApiResponses(value = {@ApiResponse(responseCode = "204")})
    @DeleteMapping("/delete-program")
    public ResponseEntity<Void> deleteProgram(@RequestParam String programName) {
        roleMappingService.deleteProgram(programName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Hard deletes a role (and all its forms/security associations of which)")
    @ApiResponses(value = {@ApiResponse(responseCode = "204")})
    @DeleteMapping("/delete-role")
    public ResponseEntity<Void> deleteRole(@RequestParam String roleName) {
        roleMappingService.deleteRole(roleName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
