package com.duoc.gasVolcan.model;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
		@NotBlank(message = "El nombre de la categoría es obligatorio") String nombre,
		String descripcion) {
}
