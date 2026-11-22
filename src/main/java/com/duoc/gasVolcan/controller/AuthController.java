package com.duoc.gasVolcan.controller;

import com.duoc.gasVolcan.model.AuthResponse;
import com.duoc.gasVolcan.model.LoginRequest;
import com.duoc.gasVolcan.model.RefreshRequest;
import com.duoc.gasVolcan.model.RegisterRequest;
import com.duoc.gasVolcan.model.UsuarioResponse;
import com.duoc.gasVolcan.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro, login, renovación de JWT y datos del usuario autenticado")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registrar cliente", description = "Crea un usuario con rol ROLE_CLIENTE")
	@ApiResponse(responseCode = "201", description = "Cliente creado y JWT entregado")
	@ApiResponse(responseCode = "400", description = "Email o username ya registrado")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	@Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un JWT firmado")
	@ApiResponse(responseCode = "200", description = "Autenticación correcta")
	@ApiResponse(responseCode = "401", description = "Credenciales inválidas")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	@Operation(summary = "Renovar token", description = "Emite un nuevo JWT a partir de uno válido")
	@ApiResponse(responseCode = "200", description = "Token renovado")
	@ApiResponse(responseCode = "401", description = "Token inválido o expirado")
	public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
		return authService.refresh(request.token());
	}

	@GetMapping("/me")
	@Operation(summary = "Usuario autenticado", description = "Devuelve los datos básicos del portador del JWT")
	@ApiResponse(responseCode = "200", description = "Sesión válida")
	@ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado")
	public UsuarioResponse me(Authentication authentication) {
		return authService.currentUser(authentication.getName());
	}
}
