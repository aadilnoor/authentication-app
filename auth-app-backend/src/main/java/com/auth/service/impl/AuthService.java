package com.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.dto.AuthResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.UserDto;
import com.auth.entity.RefreshToken;
import com.auth.entity.User;
import com.auth.enums.Provider;
import com.auth.exception.UserNotFoundException;
import com.auth.mapper.UserMapper;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;
import com.auth.service.IAuthService;
import com.auth.service.IRefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final IRefreshTokenService refreshTokenService;

	@Override
	public UserDto register(UserDto userDto) {

		log.info("Registering user: {}", userDto.email());

		if (userRepository.existsByEmail(userDto.email())) {
			throw new IllegalArgumentException("Email already exists!");
		}

		User user = userMapper.toEntity(userDto);
		user.setPassword(passwordEncoder.encode(userDto.password()));
		user.setProvider(Provider.LOCAL);

		User saved = userRepository.save(user);

		log.info("User registered successfully: {}", saved.getId());

		return userMapper.toDto(saved);
	}

	@Override
	public AuthResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new UserNotFoundException("Invalid email or password"));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new IllegalArgumentException("Invalid email or password");
		}

		// JWT access token
		String accessToken = jwtService.generateAccessToken(user.getEmail());

		// DB refresh token
		RefreshToken refreshToken = refreshTokenService.create(user.getEmail());

		return new AuthResponse(accessToken, refreshToken.getToken(), userMapper.toDto(user));
	}

	@Override
	public AuthResponse refresh(String refreshToken) {

		RefreshToken verified = refreshTokenService.verify(refreshToken);

		// Token rotation
		RefreshToken newRefreshToken = refreshTokenService.create(verified.getUserEmail());

		String newAccessToken = jwtService.generateAccessToken(verified.getUserEmail());

		UserDto user = userMapper.toDto(userRepository.findByEmail(verified.getUserEmail()).orElseThrow());

		return new AuthResponse(newAccessToken, newRefreshToken.getToken(), user);
	}

	@Override
	public void logout(String email) {
		refreshTokenService.revokeAllUserTokens(email);
	}

}
