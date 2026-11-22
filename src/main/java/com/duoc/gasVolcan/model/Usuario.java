package com.duoc.gasVolcan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre de usuario es obligatorio")
	@Column(unique = true, nullable = false)
	private String username;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "Debe ser un email válido")
	@Column(unique = true, nullable = false)
	private String email;

	@NotBlank(message = "La contraseña es obligatoria")
	@Column(nullable = false)
	private String password;

	@NotBlank(message = "El rol es obligatorio")
	@Pattern(regexp = "ROLE_(ADMIN|VENDEDOR|CLIENTE)",
			message = "El rol debe ser ROLE_ADMIN, ROLE_VENDEDOR o ROLE_CLIENTE")
	@Column(nullable = false)
	private String role;

	public Usuario() {
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	/** Normaliza ADMIN/VENDEDOR/CLIENTE a ROLE_* para evitar authorities inválidas. */
	public void setRole(String role) {
		String normalizado = Rol.normalizar(role);
		this.role = normalizado != null ? normalizado : role;
	}
}
