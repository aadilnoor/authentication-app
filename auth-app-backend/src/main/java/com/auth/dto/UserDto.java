package com.auth.dto;

import java.util.Set;
import java.util.UUID;

import com.auth.enums.Provider;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
        UUID id,
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,
        @NotBlank(message = "Password is required")
        String password,
        String image,
        Boolean enabled,
        Provider provider,
        Set<RoleDto> roles
) {}
