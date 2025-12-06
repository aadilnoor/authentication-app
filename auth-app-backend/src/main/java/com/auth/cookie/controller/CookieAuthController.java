package com.auth.cookie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.cookie.dto.CookieAuthResponse;
import com.auth.cookie.dto.CookieLoginRequest;
import com.auth.cookie.service.ICookieAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cookie-auth")
@RequiredArgsConstructor
public class CookieAuthController {

    private final ICookieAuthService cookieService;

    @PostMapping("/login")
    public ResponseEntity<CookieAuthResponse> login(
            @RequestBody CookieLoginRequest request,
            HttpServletResponse response) {

        return ResponseEntity.ok(cookieService.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<CookieAuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new RuntimeException("No refresh token");

        String refresh = null;
        for (Cookie c : cookies) {
            if (c.getName().equals("refresh_token")) refresh = c.getValue();
        }

        return ResponseEntity.ok(cookieService.refreshToken(refresh, response));
    }
}
