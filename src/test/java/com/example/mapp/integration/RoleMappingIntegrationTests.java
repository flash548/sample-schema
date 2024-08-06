package com.example.mapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class RoleMappingIntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() throws Exception {

        // create 4 roles - ADMIN, USER, MAINTAINER, DEVELOPER
        // setup 3 programs - ABC, DEF, 123

        // ABC forms -> HOME, SEARCH, LIST
        // DEF forms -> HOME, SCHEDULE, MESSAGES
        // 123 forms -> HOME, SEARCH, EMAIL

        // ABC's security functions -> CREATE, UPDATE, READ, DELETE
        // DEF's security functions -> CREATE, UPDATE, READ, DELETE, SOFT_DELETE
        // 123's security functions -> CREATE, UPDATE, READ, DELETE, LAUNCH

        for (String role : List.of("ADMIN", "USER", "MAINTAINER", "DEVELOPER")) {
            mockMvc.perform(post(String.format("/roles/add-role?roleName=%s", role)))
                    .andExpect(status().isOk());
        }

        Map<String, List<String>> programAndForms = new HashMap<>();
        programAndForms.put("ABC", List.of("HOME", "SEARCH", "LIST"));
        programAndForms.put("DEF", List.of("HOME", "SCHEDULE", "MESSAGES"));
        programAndForms.put("123", List.of("HOME", "SEARCH", "EMAIL"));

        Map<String, List<String>> programAndSecFuncs = new HashMap<>();
        programAndSecFuncs.put("ABC", List.of("CREATE", "UPDATE", "READ", "DELETE"));
        programAndSecFuncs.put("DEF", List.of("CREATE", "UPDATE", "READ", "DELETE", "SOFT_DELETE"));
        programAndSecFuncs.put("123", List.of("CREATE", "UPDATE", "READ", "DELETE", "LAUNCH"));

        for (String pgm : programAndForms.keySet()) {
            mockMvc.perform(post(String.format("/add-program?programName=%s", pgm)))
                    .andExpect(status().isOk());

            for (String form : programAndForms.get(pgm)) {
                mockMvc.perform(post(String.format("/add-form?programName=%s&formName=%s", pgm, form)))
                        .andExpect(status().isOk());
            }

            mockMvc.perform(post(String.format("/add-security-functions?programName=%s", pgm))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(programAndSecFuncs.get(pgm))))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/roleNames")).andExpect(jsonPath("$", hasSize(4)));
    }

    @Transactional
    @Test
    void testRoleMapsToProgramAndForms() throws Exception {

        // given a program ABC and ADMIN role
        // when I map C,R,U,D roles to ADMIN role for program ABC and C,R,U roles to its form named 'HOME'
        // then I expect that indication when I read it back

        mockMvc.perform(post("/map-role-to-program?roleName=ADMIN&programName=ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("CREATE", "READ", "UPDATE", "DELETE"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/programs/1")).andExpect(jsonPath("$.roleMappings.ADMIN", hasSize(4)));

        mockMvc.perform(post("/map-role-to-form?formName=HOME&roleName=ADMIN&programName=ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("CREATE", "READ", "UPDATE"))))
                .andExpect(status().isOk());

        // test the programs endpoint fetch
        mockMvc.perform(get("/programs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forms[?(@.name == 'HOME')].roleMappings.ADMIN.[*]", hasSize(3)))
                .andExpect(jsonPath("$.roleMappings.ADMIN", hasSize(4)));

        // test the roleDetails endpoint fetch
        mockMvc.perform(get("/rolesDetails?roleName=ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleName", equalTo("ADMIN")))
                .andExpect(jsonPath("$[0].programs[0].name", equalTo("ABC")))
                .andExpect(jsonPath("$[0].programs[0].forms[0].name", equalTo("HOME")))
                .andExpect(jsonPath("$[0].programs[0].forms[0].roleMappings.ADMIN", hasSize(3)))
                .andExpect(jsonPath("$[0].programs[0].roleMappings.ADMIN", hasSize(4)));
    }

    @Transactional
    @Test
    void testCollationOfRolesToResourcesCase1() throws Exception {

        // given a program `ABC` and both ADMIN and DEVELOPER roles
        // when I map Create,Read,Update,Soft_Delete,Delete to the program itself (ABC) for role ADMIN
        // when I map Read,Update,Delete roles for ADMIN role to form named 'HOME'
        // and when I map just Create, Read role for DEVELOPER role to form named 'HOME'
        // then I expect a user who has roles ADMIN and DEVELOPER will get a list of [Create,Read,Update,Delete] for the form 'HOME'
        // (since the program list of security functions are effectively ignored since there's functions defined explicitly for 'HOME' for both ADMIN and DEVELOPER)

        // admin to program mapping
        mockMvc.perform(post("/map-role-to-program?roleName=ADMIN&programName=ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("CREATE", "READ", "UPDATE", "SOFT_DELETE", "DELETE"))))
                .andExpect(status().isOk());

        // admin to form mapping
        mockMvc.perform(post("/map-role-to-form?roleName=ADMIN&formName=HOME&programName=ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("READ", "UPDATE", "DELETE"))))
                .andExpect(status().isOk());

        // developer to form mapping
        mockMvc.perform(post("/map-role-to-form?roleName=DEVELOPER&formName=HOME&programName=ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("CREATE", "READ"))))
                .andExpect(status().isOk());

        // get collated mapping
        mockMvc.perform(post("/permissions-for-program?programName=ABC&formName=HOME")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("ADMIN", "DEVELOPER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Transactional
    @Test
    void testCollationOfRolesToResourcesCase2() throws Exception {

        // given a program `ABC` and both ADMIN and DEVELOPER roles
        // when I map Read,Update,Delete roles for ADMIN role to program itself (ABC)
        // and when I map just Read role for DEVELOPER role to form named 'HOME'
        // then I expect a user who has roles ADMIN and DEVELOPER will get a list of [Read,Update,Delete] for the form 'HOME'
        // (since the FORM resource will **inherit** the Program ABC's ADMIN roles since there's none specified directly)

        // admin to program mapping
        mockMvc.perform(post("/map-role-to-program?roleName=ADMIN&programName=ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("READ", "UPDATE", "DELETE"))))
                .andExpect(status().isOk());

        // developer to form mapping
        mockMvc.perform(post("/map-role-to-form?roleName=DEVELOPER&formName=HOME&programName=ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("READ"))))
                .andExpect(status().isOk());

        // get collated mapping
        mockMvc.perform(post("/permissions-for-program?programName=ABC&formName=HOME")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of("ADMIN", "DEVELOPER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
