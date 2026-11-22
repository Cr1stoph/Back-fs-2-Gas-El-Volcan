package com.duoc.gasVolcan.controller;

import java.util.List;

import com.duoc.gasVolcan.model.Categoria;
import com.duoc.gasVolcan.model.CategoriaRequest;
import com.duoc.gasVolcan.service.CategoriaService;

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
@RequestMapping("/api/v1/categorias")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categorías", description = "Administración de CATEGORIA. Acceso exclusivo de ADMINISTRADOR")
public class CategoriaController {

	private final CategoriaService service;

	public CategoriaController(CategoriaService service) {
		this.service = service;
	}

	@GetMapping
	@Operation(summary = "Listar categorías", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "200", description = "Listado obtenido")
	public List<Categoria> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Detalle de categoría", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "200", description = "Categoría encontrada")
	@ApiResponse(responseCode = "404", description = "Categoría no encontrada")
	public Categoria findById(@PathVariable Long id) {
		return service.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear categoría", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "201", description = "Categoría creada")
	public Categoria create(@Valid @RequestBody CategoriaRequest request) {
		return service.create(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar categoría", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "200", description = "Categoría actualizada")
	public Categoria update(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
		return service.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Eliminar categoría", description = "Sólo ADMINISTRADOR; falla si tiene productos")
	@ApiResponse(responseCode = "204", description = "Categoría eliminada")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}
}
