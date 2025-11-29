package com.auth.dto;

import java.time.LocalDateTime;

public record ApiError(
        boolean success,
        String message,
        int status,
        String path,
        LocalDateTime timestamp
) { 
	public ApiError(String message, int status, String path) {
        this(false, message, status, path, LocalDateTime.now());
    }
}
