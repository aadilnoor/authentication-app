package com.auth.service.impl;

import com.auth.entity.RefreshToken;
import com.auth.repository.RefreshTokenRepository;
import com.auth.service.IRefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService implements IRefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepo;

	@Value("${jwt.refresh-expiration-ms}")
	private long refreshExpiryMs;

	@Override
	public RefreshToken create(String email) {

		// delete old tokens
		refreshTokenRepo.deleteByUserEmail(email);

		RefreshToken token = RefreshToken.builder()
				.token(java.util.UUID.randomUUID().toString() + "." + java.util.UUID.randomUUID())
				.expiry(Instant.now().plusMillis(refreshExpiryMs)).userEmail(email).revoked(false).expired(false)
				.build();

		refreshTokenRepo.save(token);

		log.info("Refresh token created for {}", email);
		return token;
	}

	@Override
	public RefreshToken verify(String token) {

		RefreshToken savedToken = refreshTokenRepo.findByToken(token)
				.orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

		if (savedToken.isRevoked()) {
			throw new IllegalArgumentException("Refresh token is revoked");
		}

		if (savedToken.getExpiry().isBefore(Instant.now())) {
			savedToken.setExpired(true);
			refreshTokenRepo.save(savedToken);
			throw new IllegalArgumentException("Refresh token expired");
		}

		return savedToken;
	}

	@Override
	public void revokeAllUserTokens(String email) {
		refreshTokenRepo.deleteByUserEmail(email);
	}
}
