package com.duoc.gasVolcan.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import com.duoc.gasVolcan.config.ApiExceptionHandler.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handle(
			HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ApiError error = new ApiError(
				HttpStatus.FORBIDDEN.value(),
				"Acceso denegado: No cuenta con los permisos necesarios para realizar esta acción",
				Instant.now(),
				Map.of()
		);

		response.getWriter().write(objectMapper.writeValueAsString(error));
	}
}
