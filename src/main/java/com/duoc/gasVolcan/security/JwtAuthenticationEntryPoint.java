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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		ApiError error = new ApiError(
				HttpStatus.UNAUTHORIZED.value(),
				"No autorizado: Se requiere autenticación válida para acceder a este recurso",
				Instant.now(),
				Map.of()
		);
		
		response.getWriter().write(objectMapper.writeValueAsString(error));
	}
}
