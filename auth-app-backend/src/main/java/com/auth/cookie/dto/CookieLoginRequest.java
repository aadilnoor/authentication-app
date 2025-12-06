package com.auth.cookie.dto;

public record CookieLoginRequest(
        String email,
        String password
) {}
