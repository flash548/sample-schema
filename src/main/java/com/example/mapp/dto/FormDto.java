package com.example.mapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class FormDto {

    // ignore for now
    @JsonIgnore
    Long id;

    String name;
    Map<String, List<String>> roleMappings;
}
