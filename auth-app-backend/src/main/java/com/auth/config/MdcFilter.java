package com.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MdcFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestId = generateReadableRequestId();

		MDC.put("requestId", requestId);

		response.setHeader("X-Request-ID", requestId);

		try {
			filterChain.doFilter(request, response);
		} 
		finally {
			MDC.clear();
		}
	}

	private String generateReadableRequestId() {
		int number = ThreadLocalRandom.current().nextInt(10000, 99999);
		return "REQ-" + number;
	}
}
