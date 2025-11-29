package com.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auth.dto.UserDto;
import com.auth.entity.User;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserDto dto);

    UserDto toDto(User user);
}
