package com.duoc.gasVolcan.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
		@NotBlank(message = "El nombre de usuario es obligatorio") String username,
		@NotBlank(message = "El email es obligatorio") @Email(message = "Debe ser un email válido") String email,
		@NotBlank(message = "La contraseña es obligatoria") String password,
		String role) {
}
