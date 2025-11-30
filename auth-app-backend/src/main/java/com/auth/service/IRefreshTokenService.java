package com.auth.service;

import com.auth.entity.RefreshToken;

public interface IRefreshTokenService {
	RefreshToken create(String email);

	RefreshToken verify(String token);

	void revokeAllUserTokens(String email);
}
