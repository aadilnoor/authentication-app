package com.auth.cookie.service.impl;

import com.auth.cookie.dto.CookieAuthResponse;
import com.auth.cookie.dto.CookieLoginRequest;
import com.auth.cookie.service.ICookieAuthService;
import com.auth.entity.RefreshToken;
import com.auth.entity.User;
import com.auth.exception.UserNotFoundException;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;
import com.auth.service.IRefreshTokenService;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieAuthService implements ICookieAuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;       // <--- Use ONE jwt service
    private final IRefreshTokenService refreshTokenService;

    private static final long REFRESH_TTL_SECONDS = 7L * 24 * 60 * 60;

    @Override
    public CookieAuthResponse login(CookieLoginRequest request, HttpServletResponse response) {

        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Generate Access Token (SAME jwtService used everywhere)
        String access = jwtService.generateAccessToken(user.getEmail());

        // DB Refresh Token
        RefreshToken refreshEntry = refreshTokenService.create(user.getEmail());

        // Set cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshEntry.getToken())
                .httpOnly(true)
                .secure(false)   // set true in production
                .path("/")
                .sameSite("Lax")
                .maxAge(REFRESH_TTL_SECONDS)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return new CookieAuthResponse(true, "Login successful", access);
    }

    @Override
    public CookieAuthResponse refreshToken(String refreshToken, HttpServletResponse response) {

        RefreshToken saved = refreshTokenService.verify(refreshToken);

        refreshTokenService.revokeAllUserTokens(saved.getUserEmail());
        RefreshToken newToken = refreshTokenService.create(saved.getUserEmail());

        String newAccess = jwtService.generateAccessToken(saved.getUserEmail());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", newToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(REFRESH_TTL_SECONDS)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return new CookieAuthResponse(true, "Token refreshed", newAccess);
    }
}
