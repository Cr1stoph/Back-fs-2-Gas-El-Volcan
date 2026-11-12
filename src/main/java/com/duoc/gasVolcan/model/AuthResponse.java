package com.duoc.gasVolcan.model;

public record AuthResponse(
		String token,
		String username,
		String email,
		String role) {
}
