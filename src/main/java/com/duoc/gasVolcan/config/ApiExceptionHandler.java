package com.duoc.gasVolcan.config;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ApiError handleStatus(ResponseStatusException exception) {
		return new ApiError(exception.getStatusCode().value(), exception.getReason(), Instant.now(), Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiError handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> fields = exception.getBindingResult().getFieldErrors().stream()
				.collect(java.util.stream.Collectors.toMap(
						error -> error.getField(),
						error -> error.getDefaultMessage() == null ? "Valor inválido" : error.getDefaultMessage(),
						(first, ignored) -> first));
		return new ApiError(HttpStatus.BAD_REQUEST.value(), "La solicitud contiene datos inválidos", Instant.now(),
				fields);
	}

	public record ApiError(int status, String message, Instant timestamp, Map<String, String> fields) {
	}
}



