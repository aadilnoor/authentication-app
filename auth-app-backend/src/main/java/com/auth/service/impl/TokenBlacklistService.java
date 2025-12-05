package com.auth.service.impl;

import com.auth.entity.TokenBlacklist;
import com.auth.repository.TokenBlacklistRepository;
import com.auth.service.ITokenBlacklistService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService implements ITokenBlacklistService {

    private final TokenBlacklistRepository repo;

    @Override
    public void blacklist(String token, long expiryMs) {

        TokenBlacklist entry = TokenBlacklist.builder()
                .token(token)
                .expiry(Instant.now().plusMillis(expiryMs))
                .build();

        repo.save(entry);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return repo.existsByToken(token);
    }
}
