package com.duoc.gasVolcan.controller;

import java.util.List;

import com.duoc.gasVolcan.model.Usuario;
import com.duoc.gasVolcan.model.UsuarioRequest;
import com.duoc.gasVolcan.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Administración de USUARIO. Acceso exclusivo de ADMINISTRADOR")
public class UsuarioController {

	private final UsuarioService service;

	public UsuarioController(UsuarioService service) {
		this.service = service;
	}

	@GetMapping
	@Operation(summary = "Listar usuarios", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "200", description = "Listado obtenido")
	public List<Usuario> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Detalle de usuario", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "200", description = "Usuario encontrado")
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	public Usuario findById(@PathVariable Long id) {
		return service.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear usuario", description = "Permite asignar ADMIN, VENDEDOR o CLIENTE")
	@ApiResponse(responseCode = "201", description = "Usuario creado")
	public Usuario create(@Valid @RequestBody UsuarioRequest request) {
		return service.create(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar usuario", description = "Actualiza datos y rol del usuario")
	@ApiResponse(responseCode = "200", description = "Usuario actualizado")
	public Usuario update(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
		return service.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Eliminar usuario", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "204", description = "Usuario eliminado")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}
}
