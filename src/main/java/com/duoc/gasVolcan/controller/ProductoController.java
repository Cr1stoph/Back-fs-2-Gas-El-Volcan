package com.duoc.gasVolcan.controller;

import java.util.List;

import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.model.ProductoRequest;
import com.duoc.gasVolcan.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Catálogo público de lectura; escritura restringida a ADMINISTRADOR")
public class ProductoController {

	private final ProductoService service;

	public ProductoController(ProductoService service) {
		this.service = service;
	}

	@GetMapping
	@Operation(summary = "Listar productos", description = "Lista el catálogo, opcionalmente filtrado por categoría")
	@ApiResponse(responseCode = "200", description = "Listado obtenido")
	public List<Producto> findAll(
			@Parameter(description = "Filtra por nombre de categoría") @RequestParam(required = false) String categoria) {
		return categoria == null || categoria.isBlank() ? service.findAll() : service.findByCategoria(categoria);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Detalle de producto")
	@ApiResponse(responseCode = "200", description = "Producto encontrado")
	@ApiResponse(responseCode = "404", description = "Producto no encontrado")
	public Producto findById(@PathVariable Long id) {
		return service.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Crear producto", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "201", description = "Producto creado")
	@ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
	public Producto create(@Valid @RequestBody ProductoRequest request) {
		return service.create(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Actualizar producto", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "200", description = "Producto actualizado")
	@ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
	public Producto update(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
		return service.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Eliminar producto", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "204", description = "Producto eliminado")
	@ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}
}
