package com.duoc.gasVolcan.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductoRequest(
		@NotBlank String nombre,
		@NotNull @DecimalMin("0.01") BigDecimal precio,
		@DecimalMin("0.01") BigDecimal precioOferta,
		String img,
		@Min(0) int stock,
		@NotBlank String categoria) {
}



