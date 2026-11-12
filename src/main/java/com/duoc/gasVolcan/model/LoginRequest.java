package com.duoc.gasVolcan.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank(message = "El email o usuario es obligatorio") String email,
		@NotBlank(message = "La contraseña es obligatoria") String password) {
}
