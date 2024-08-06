package com.example.mapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class IdentityTests {

    @Test
    public void testIdForm() {
        Form f1 = Form.builder().name("TEST").build();
        Form f2 = Form.builder().name("test").build();

        assertEquals(f1, f2);
    }

    @Test
    public void testIdProgram() {
        Program f1 = Program.builder().name("TEST").build();
        Program f2 = Program.builder().name("test").build();

        assertEquals(f1, f2);
    }

    @Test
    public void testIdSecurityFunction() {

        // should take into account name AND the owning program

        SecurityFunction f1 = SecurityFunction.builder().programId(1L).name("TEST").build();
        SecurityFunction f2 = SecurityFunction.builder().programId(1L).name("test").build();

        assertEquals(f1, f2);

        f2.setProgramId(2L);

        assertNotEquals(f1, f2);
    }

    @Test
    public void testIdRole() {
        Role f1 = Role.builder().name("TEST").build();
        Role f2 = Role.builder().name("test").build();

        assertEquals(f1, f2);

    }
}
