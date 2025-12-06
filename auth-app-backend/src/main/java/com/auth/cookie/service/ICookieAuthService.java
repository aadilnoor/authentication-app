package com.auth.cookie.service;


import com.auth.cookie.dto.CookieAuthResponse;
import com.auth.cookie.dto.CookieLoginRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface ICookieAuthService {

    CookieAuthResponse login(CookieLoginRequest request, HttpServletResponse response);

    CookieAuthResponse refreshToken(String refreshToken, HttpServletResponse response);
}
