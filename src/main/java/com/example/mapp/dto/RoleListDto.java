package com.example.mapp.dto;

import com.example.mapp.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RoleListDto {

    List<Role> roles;
}
