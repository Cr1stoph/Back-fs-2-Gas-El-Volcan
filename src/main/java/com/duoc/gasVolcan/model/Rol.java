package com.duoc.gasVolcan.model;

/**
 * Los tres roles exigidos por la rúbrica.
 * Se guardan en Usuario.role con el prefijo ROLE_ que espera Spring Security.
 */
public enum Rol {
	ADMIN("ROLE_ADMIN"),
	VENDEDOR("ROLE_VENDEDOR"),
	CLIENTE("ROLE_CLIENTE");

	private final String authority;

	Rol(String authority) {
		this.authority = authority;
	}

	public String authority() {
		return authority;
	}

	public static boolean esValido(String valor) {
		if (valor == null) {
			return false;
		}
		for (Rol rol : values()) {
			if (rol.authority.equalsIgnoreCase(valor)
					|| rol.name().equalsIgnoreCase(valor)) {
				return true;
			}
		}
		return false;
	}

	public static String normalizar(String valor) {
		if (valor == null) {
			return null;
		}
		for (Rol rol : values()) {
			if (rol.authority.equalsIgnoreCase(valor)
					|| rol.name().equalsIgnoreCase(valor)) {
				return rol.authority;
			}
		}
		return null;
	}
}
