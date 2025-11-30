package com.auth.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth.dto.ApiError;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {

		log.error("UserNotFoundException at {} - {}", request.getRequestURI(), ex.getMessage());

		ApiError error = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {

		log.error("IllegalArgumentException at {} - {}", request.getRequestURI(), ex.getMessage());

		ApiError error = new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage()).findFirst().orElse("Validation error");

		log.error("Validation error at {} - {}", request.getRequestURI(), message);

		ApiError error = new ApiError(message, HttpStatus.BAD_REQUEST.value(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest request) {

		log.error("Unhandled exception at {} - {}", request.getRequestURI(), ex.getMessage(), ex);

		ApiError error = new ApiError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value(),
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(io.jsonwebtoken.security.WeakKeyException.class)
	public ResponseEntity<ApiError> handleWeakKey(WeakKeyException ex, HttpServletRequest req) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "JWT secret key is too weak", req);
	}

	@ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
	public ResponseEntity<ApiError> handleInvalidSignature(SignatureException ex, HttpServletRequest req) {
		return error(HttpStatus.UNAUTHORIZED, "Invalid JWT signature", req);
	}

	@ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
	public ResponseEntity<ApiError> handleExpiredToken(ExpiredJwtException ex, HttpServletRequest req) {
		return error(HttpStatus.UNAUTHORIZED, "JWT token expired", req);
	}

	@ExceptionHandler(io.jsonwebtoken.MalformedJwtException.class)
	public ResponseEntity<ApiError> handleMalformedToken(MalformedJwtException ex, HttpServletRequest req) {
		return error(HttpStatus.BAD_REQUEST, "Invalid JWT token", req);
	}

	@ExceptionHandler(io.jsonwebtoken.UnsupportedJwtException.class)
	public ResponseEntity<ApiError> handleUnsupportedToken(UnsupportedJwtException ex, HttpServletRequest req) {
		return error(HttpStatus.BAD_REQUEST, "Unsupported JWT token", req);
	}

	@ExceptionHandler(io.jsonwebtoken.io.DecodingException.class)
	public ResponseEntity<ApiError> handleDecodingError(DecodingException ex, HttpServletRequest req) {
		return error(HttpStatus.BAD_REQUEST, "Token decoding failed", req);
	}

	private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest req) {
		ApiError err = new ApiError(false, message, status.value(), req.getRequestURI(), LocalDateTime.now());

		return ResponseEntity.status(status).body(err);
	}
}
