package com.auth.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.dto.UserDto;
import com.auth.entity.User;
import com.auth.enums.Provider;
import com.auth.exception.UserNotFoundException;
import com.auth.mapper.UserMapper;
import com.auth.repository.UserRepository;
import com.auth.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	@Override
	public UserDto createUser(UserDto userDto) {

		log.info("Creating user with email: {}", userDto.email());

		if (userDto.email() == null || userDto.email().isBlank()) {
			log.warn("Email is blank or null");
			throw new IllegalArgumentException("Email is required");
		}

		if (userRepository.existsByEmail(userDto.email())) {
			log.error("Email already exists: {}", userDto.email());
			throw new IllegalArgumentException("Email already exists");
		}

		User user = userMapper.toEntity(userDto);
		user.setPassword(passwordEncoder.encode(userDto.password()));

		if (user.getProvider() == null) {
			user.setProvider(Provider.LOCAL);
		}

		User savedUser = userRepository.save(user);
		log.info("User created successfully with id: {}", savedUser.getId());
		return userMapper.toDto(savedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserByEmail(String email) {

		log.info("Fetching user by email: {}", email);

		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email cannot be null or blank");
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

		return userMapper.toDto(user);
	}

	@Override
	@Transactional
	public UserDto updateUser(UserDto userDto, UUID uuid) {

		log.info("Updating user with ID: {}", uuid);

		if (uuid == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}

		User existingUser = userRepository.findById(uuid)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + uuid));

		if (userDto.username() != null && !userDto.username().isBlank()) {
			existingUser.setUsername(userDto.username());
		}

		if (userDto.email() != null && !userDto.email().isBlank()) {
			existingUser.setEmail(userDto.email());
		}

		if (userDto.image() != null) {
			existingUser.setImage(userDto.image());
		}

		if (userDto.enabled() != null) {
			existingUser.setEnabled(userDto.enabled());
		}

		if (userDto.provider() != null) {
			existingUser.setProvider(userDto.provider());
		}

		User updatedUser = userRepository.save(existingUser);

		log.info("User updated successfully with ID: {}", updatedUser.getId());

		return userMapper.toDto(updatedUser);
	}

	@Override
	@Transactional
	public void deleteUser(UUID uuid) {

		log.info("Deleting user with ID: {}", uuid);

		if (uuid == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}

		User user = userRepository.findById(uuid)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + uuid));

		userRepository.delete(user);

		log.info("User deleted successfully with ID: {}", uuid);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserById(UUID uuid) {

		if (uuid == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}

		log.info("Fetching user by id: {}", uuid);

		User user = userRepository.findById(uuid)
				.orElseThrow(() -> new UserNotFoundException("User not found with this id: " + uuid));

		return userMapper.toDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAllUsers() {

		log.info("Fetching all users");

		return userRepository.findAll().stream().map(userMapper::toDto).toList();
	}

}
