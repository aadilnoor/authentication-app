package com.auth.service;

import com.auth.dto.AuthResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.UserDto;

public interface IAuthService {

	UserDto register(UserDto userDto);

	AuthResponse login(LoginRequest request);

	AuthResponse refresh(String refreshToken);

	void logout(String email);
	void logout(String email, String accessToken);


}
