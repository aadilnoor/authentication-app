package com.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.ApiResponse;
import com.auth.dto.AuthResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.RefreshTokenRequest;
import com.auth.dto.UserDto;
import com.auth.service.IAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final IAuthService authService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody UserDto userDto) {

		UserDto createdUser = authService.register(userDto);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse<>(true, "User registered successfully", createdUser));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
		AuthResponse auth = authService.login(request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", auth));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest request) {

		AuthResponse response = authService.refresh(request.refreshToken());

		return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed", response));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(@RequestParam String email) {

		authService.logout(email);

		return ResponseEntity.ok(new ApiResponse<>(true, "Logged out", "Logout successful"));
	}

}
