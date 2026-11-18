package com.duoc.gasVolcan.model;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
		@NotBlank(message = "El token es obligatorio") String token) {
}
