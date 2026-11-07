package com.duoc.gasVolcan.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrdenRequest(
		@NotBlank String nombre,
		@NotBlank @Email String correo,
		@NotBlank String telefono,
		String direccion,
		@NotBlank String tipoEntrega,
		@NotBlank String formaPago,
		String numeroTarjeta,
		@NotEmpty List<@Valid OrdenItemRequest> items) {
}



