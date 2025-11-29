package com.auth.service;

import java.util.List;
import java.util.UUID;

import com.auth.dto.UserDto;

public interface IUserService {

	UserDto createUser(UserDto userDto);

	UserDto getUserByEmail(String email);

	UserDto updateUser(UserDto userDto, UUID uuid);

	void deleteUser(UUID uuid);

	UserDto getUserById(UUID uuid);

	List<UserDto> getAllUsers();
}
