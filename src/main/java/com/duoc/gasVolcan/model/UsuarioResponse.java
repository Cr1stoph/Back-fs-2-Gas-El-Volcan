package com.duoc.gasVolcan.model;

/** Datos básicos del usuario autenticado (sin password). */
public record UsuarioResponse(Long id, String username, String email, String role) {
}
