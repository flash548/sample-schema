package com.example.mapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleFunctionMappingTests {

    @Test
    public void testId() {
        RoleFunctionMapping r = new RoleFunctionMapping();
        r.setId(new RoleFunctionMapping.RoleFunctionMappingId(1L, 1L, 1L));

        RoleFunctionMapping s = new RoleFunctionMapping();
        s.setId(new RoleFunctionMapping.RoleFunctionMappingId(22L, 22L, 21L));

        RoleFunctionMapping t = new RoleFunctionMapping();
        t.setId(new RoleFunctionMapping.RoleFunctionMappingId(1L, 1L, 1L));

        assertFalse(r.equals(s));
        assertTrue(r.equals(t));
    }
}
