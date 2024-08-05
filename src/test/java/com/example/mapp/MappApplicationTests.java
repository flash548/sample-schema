package com.example.mapp;

import com.example.mapp.model.Program;
import com.example.mapp.model.Role;
import com.example.mapp.repository.ProgramRepository;
import com.example.mapp.repository.RoleRepository;
import com.example.mapp.service.RoleMappingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
        roleRepository.save(Role.builder()
                .name("ADMIN")
                .build());
    }

    @Test
    @Transactional
    void testProgramSecurityFunctions() {

        List<Program> savedPrograms = programRepository.saveAll(
                List.of(Program.builder()
                                .name("OISAABC123")
                                .build(),
                        Program.builder()
                                .name("OISAABC456")
                                .build()));

        roleMappingService.addSecurityFunctionsToProgram("OISAABC123", List.of("CREATE", "create", "READ", "UPDATE"));
        var p = roleMappingService.getProgramById(savedPrograms.get(0).getId());

        // verify we added three security functions despite giving it 4 (de-duped, case-insensitive)
        assertTrue(p.getSecurityFunctions().size() == 3);

        // add ADMIN role to this program with CREATE, READ, UPDATE (intentionally duplicates to check they're unique'd)
        p = roleMappingService.associateRoleToProgram("OISAABC123",
                "ADMIN",
                List.of("CREATE", "READ", "UPDATE", "CREATE", "READ", "UPDATE"));

        // assert 3 mappings total
        assertTrue(p.getRoleFunctionMappings().size() == 3);

        // add USER role with just READ
        var u = roleMappingService.associateRoleToProgram("OISAABC123",
                "USER",
                List.of("READ"));

        // assert 4 mappings total
        assertTrue(p.getRoleFunctionMappings().size() == 4);

//        // take away ADMIN's CREATE ability, check no orphans
//        p = roleMappingService.associateRoleToProgram("OISAABC123",
//                "ADMIN",
//                List.of("READ", "UPDATE"));
//
//        // assert 3 mappings total now
//        assertTrue(p.getRoleFunctionMappings().size() == 3);
    }

}
