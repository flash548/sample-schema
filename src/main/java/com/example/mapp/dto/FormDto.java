package com.example.mapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class FormDto {

    Long id;
    String name;
    Map<String, List<String>> roleMappings;
}
