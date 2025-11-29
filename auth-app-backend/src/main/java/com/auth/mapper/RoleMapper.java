package com.auth.mapper;

import org.mapstruct.Mapper;

import com.auth.dto.RoleDto;
import com.auth.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Role role);
    Role toEntity(RoleDto dto);
}
