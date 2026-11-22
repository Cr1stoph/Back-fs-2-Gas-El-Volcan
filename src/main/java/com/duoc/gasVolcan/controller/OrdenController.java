package com.duoc.gasVolcan.controller;

import java.util.List;

import com.duoc.gasVolcan.model.Orden;
import com.duoc.gasVolcan.model.OrdenRequest;
import com.duoc.gasVolcan.service.OrdenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedidos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pedidos / Boletas", description = "Consultas para ADMIN y VENDEDOR; CLIENTE crea y consulta SUS pedidos")
public class OrdenController {

	private final OrdenService service;

	public OrdenController(OrdenService service) {
		this.service = service;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','VENDEDOR')")
	@Operation(summary = "Listar todas las boletas", description = "Sólo ADMINISTRADOR y VENDEDOR")
	@ApiResponse(responseCode = "200", description = "Listado obtenido")
	@ApiResponse(responseCode = "403", description = "Rol sin permiso para ver todas las boletas")
	public List<Orden> findAll() {
		return service.findAll();
	}

	@GetMapping("/mios")
	@Operation(summary = "Mis boletas", description = "Pedidos pertenecientes al CLIENTE autenticado")
	@ApiResponse(responseCode = "200", description = "Listado de pedidos propios")
	public List<Orden> findMios(Authentication authentication) {
		return service.findByCliente(authentication.getName());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Detalle de boleta", description = "ADMIN/VENDEDOR ven cualquier pedido; CLIENTE sólo los suyos")
	@ApiResponse(responseCode = "200", description = "Pedido encontrado")
	@ApiResponse(responseCode = "403", description = "El pedido no pertenece al CLIENTE")
	@ApiResponse(responseCode = "404", description = "Pedido no encontrado")
	public Orden findById(@PathVariable String id, Authentication authentication) {
		if (esCliente(authentication)) {
			return service.findByIdParaCliente(id, authentication.getName());
		}
		return service.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear boleta/pedido", description = "Flujo de compra; calcula totales y descuenta stock")
	@ApiResponse(responseCode = "201", description = "Pedido creado")
	@ApiResponse(responseCode = "400", description = "Datos de entrega, pago o stock inválidos")
	public Orden create(@Valid @RequestBody OrdenRequest request, Authentication authentication) {
		String email = authentication == null ? null : authentication.getName();
		return service.create(request, email);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar boleta", description = "Sólo ADMINISTRADOR, mientras esté pendiente")
	@ApiResponse(responseCode = "200", description = "Pedido actualizado")
	@ApiResponse(responseCode = "409", description = "El pedido ya no se puede editar")
	public Orden update(@Parameter(description = "ID del pedido") @PathVariable String id,
			@Valid @RequestBody OrdenRequest request) {
		return service.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar boleta", description = "Sólo ADMINISTRADOR; restaura el stock")
	@ApiResponse(responseCode = "204", description = "Pedido eliminado")
	public void delete(@PathVariable String id) {
		service.delete(id);
	}

	@PatchMapping("/{id}/estado")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Avanzar estado del pedido", description = "Sólo ADMINISTRADOR")
	@ApiResponse(responseCode = "200", description = "Estado actualizado")
	@ApiResponse(responseCode = "409", description = "Transición de estado no permitida")
	public Orden advance(@PathVariable String id) {
		return service.advance(id);
	}

	private boolean esCliente(Authentication authentication) {
		if (authentication == null) {
			return false;
		}
		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_CLIENTE"::equals);
	}
}
