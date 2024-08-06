package com.example.mapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class ProgramDto {

    Long id;
    String name;
    List<FormDto> forms;
    Map<String, List<String>> roleMappings;
}
