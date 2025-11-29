package com.auth.dto;

import java.util.UUID;

public record RoleDto(
        UUID id,
        String name
) {}
