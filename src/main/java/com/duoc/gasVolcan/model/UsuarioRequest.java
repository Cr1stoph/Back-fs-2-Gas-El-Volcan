package com.duoc.gasVolcan.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Solicitud de administración de usuarios (CREAR / ACTUALIZAR). */
public record UsuarioRequest(
		@NotBlank String username,
		@NotBlank @Email String email,
		String password,
		@NotBlank
		@Pattern(regexp = "ROLE_(ADMIN|VENDEDOR|CLIENTE)|ADMIN|VENDEDOR|CLIENTE",
				message = "El rol debe ser ADMIN, VENDEDOR o CLIENTE")
		String role) {
}
