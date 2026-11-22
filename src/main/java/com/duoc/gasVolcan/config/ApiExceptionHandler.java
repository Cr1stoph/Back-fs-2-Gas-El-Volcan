package com.duoc.gasVolcan.config;

import java.time.Instant;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiError> handleStatus(ResponseStatusException exception) {
		HttpStatusCode status = exception.getStatusCode();
		ApiError error = new ApiError(status.value(), exception.getReason(), Instant.now(), Map.of());
		return ResponseEntity.status(status).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> fields = exception.getBindingResult().getFieldErrors().stream()
				.collect(java.util.stream.Collectors.toMap(
						error -> error.getField(),
						error -> error.getDefaultMessage() == null ? "Valor inválido" : error.getDefaultMessage(),
						(first, ignored) -> first));
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "La solicitud contiene datos inválidos",
				Instant.now(), fields);
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class })
	public ResponseEntity<ApiError> handleIntegrity(Exception exception) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Datos duplicados o inválidos",
				Instant.now(), Map.of());
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Ocurrió un error inesperado", Instant.now(), Map.of());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	public record ApiError(int status, String message, Instant timestamp, Map<String, String> fields) {
	}
}
