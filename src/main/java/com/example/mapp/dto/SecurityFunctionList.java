package com.example.mapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SecurityFunctionList {

    List<String> securityFunctions;
}
