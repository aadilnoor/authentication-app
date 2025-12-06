package com.auth.cookie.dto;

public record CookieAuthResponse(
        boolean success,
        String message,
        String accessToken
) {}
