package com.duoc.gasVolcan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
	@Column(nullable = false)
	private String role; // e.g. "ROLE_ADMIN", "ROLE_CLIENTE"
}
