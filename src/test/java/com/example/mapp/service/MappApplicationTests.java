package com.example.mapp.service;

import com.example.mapp.model.Form;
import com.example.mapp.model.Role;
import com.example.mapp.repository.ProgramRepository;
import com.example.mapp.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
class MappApplicationTests {

    @Autowired
    RoleMappingService roleMappingService;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    void setup() {
        roleRepository.save(Role.builder().name("ADMIN").build());
    }

    @Test
    @Transactional
    void testFormRemoval() {
        var p1 = roleMappingService.createProgram("AABC123");
        roleMappingService.addFormToProgram(p1.getName(), "form1");
        roleMappingService.addFormToProgram(p1.getName(), "form2");

        roleMappingService.associateRoleToForm(p1.getName(), "form2", "ADMIN", List.of("CREATE", "UPDATE"));

        assertEquals(2, p1.getForms().size());
        assertEquals(2, p1.getSecurityFunctions().size());

        p1 = roleMappingService.removeFormFromProgram(p1.getName(), "form2");
        assertEquals(1, p1.getForms().size());
        assertEquals(2, p1.getSecurityFunctions().size());
    }

    @Test
    @Transactional
    void testSecurityFunctionRemoval() {
        var p1 = roleMappingService.createProgram("AABC123");
        roleMappingService.addFormToProgram(p1.getName(), "form1");
        roleMappingService.addFormToProgram(p1.getName(), "form2");

        roleMappingService.associateRoleToForm(p1.getName(), "form2", "ADMIN", List.of("CREATE", "UPDATE"));

        assertEquals(2, p1.getForms().size());
        assertEquals(2, p1.getSecurityFunctions().size());

        p1 = roleMappingService.removeSecurityFunctionFromProgram(p1.getName(), "CREATE");
        assertEquals(2, p1.getForms().size());

        // test form2 only has UPDATE on it now
        assertEquals(1,
                p1.getForms()
                        .stream()
                        .filter(p -> p.getName().equals("FORM2"))
                        .findFirst()
                        .get()
                        .getRoleFunctionFormMappings()
                        .size());

        // entire program just has one SecFunc on it now ("UPDATE")
        assertEquals(1, p1.getSecurityFunctions().size());
    }

    @Test
    @Transactional
    void testRoleProgramSecurityFunctions() {

        // create 2 programs
        var p1 = roleMappingService.createProgram("AABC123");
        roleMappingService.createProgram("AABC456");

        roleMappingService.addSecurityFunctionsToProgram("AABC123", List.of("CREATE", "create", "READ", "UPDATE"));
        var p = roleMappingService.getProgramById(p1.getId());

        // verify we added three security functions despite giving it 4 (de-duped, case-insensitive)
        assertTrue(p.getSecurityFunctions().size() == 3);

        // add ADMIN role to this program with CREATE, READ, UPDATE (intentionally duplicates to check they're unique'd)
        p = roleMappingService.associateRoleToProgram("AABC123",
                "ADMIN",
                List.of("CREATE", "READ", "UPDATE", "CREATE", "READ", "UPDATE"));

        // assert 3 mappings total
        assertEquals(3, p.getRoleFunctionMappings().size());

        // add USER role with just READ
        var u = roleMappingService.associateRoleToProgram("AABC123", "USER", List.of("READ"));

        // assert 4 mappings total
        assertEquals(4, u.getRoleFunctionMappings().size());

        // take away ADMIN's CREATE ability, check no orphans (orphans removed from previous ADMIN setting)
        p = roleMappingService.associateRoleToProgram("AABC123", "ADMIN", List.of("READ", "UPDATE"));

        // assert 3 mappings total now
        assertEquals(3, p.getRoleFunctionMappings().size());
    }

    @Test
    @Transactional
    void testRoleProgramFormSecuritySettings() {

        // create two programs
        var p1 = roleMappingService.createProgram("AABC123");
        roleMappingService.createProgram("AABC456");

        roleMappingService.addSecurityFunctionsToProgram("AABC123", List.of("CREATE", "READ", "UPDATE"));
        var p = roleMappingService.getProgramById(p1.getId());

        // verify we added three security functions to the program AABC123
        assertTrue(p.getSecurityFunctions().size() == 3);

        p = roleMappingService.addFormToProgram("AABC123", "Form1");
        p = roleMappingService.addFormToProgram("AABC123", "Form2");

        // verify we added 2 forms
        assertTrue(p.getForms().size() == 2);

        // associate roles to security functions to the forms - just grant "DELETE" to ADMIN on Form1
        p = roleMappingService.associateRoleToForm("AABC123", "Form1", "ADMIN", List.of("DELETE"));

        assertTrue(p.getForms().size() == 2);

        // assert we have one role for ADMIN on Form1
        Form f = p.getFormNamed("Form1").orElseThrow();
        assertEquals(1, f.getRoleFunctionFormMappings().size());
        assertTrue(f.getRoleFunctionFormMappings().stream().toList().get(0).getRole().getName().equals("ADMIN"));
    }

}
