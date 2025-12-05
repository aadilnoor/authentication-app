package com.auth.service;

public interface ITokenBlacklistService {

	void blacklist(String token, long expiryMs);

    boolean isBlacklisted(String token);
}
