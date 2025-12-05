package com.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.auth.entity.TokenBlacklist;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {

    boolean existsByToken(String token);
}
