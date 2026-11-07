package com.duoc.gasVolcan.controller;

import java.util.List;

import com.duoc.gasVolcan.model.Orden;
import com.duoc.gasVolcan.model.OrdenRequest;
import com.duoc.gasVolcan.service.OrdenService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class OrdenController {

	private final OrdenService service;

	public OrdenController(OrdenService service) {
		this.service = service;
	}

	@GetMapping
	public List<Orden> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public Orden findById(@PathVariable String id) {
		return service.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Orden create(@Valid @RequestBody OrdenRequest request) {
		return service.create(request);
	}

	@PatchMapping("/{id}/estado")
	public Orden advance(@PathVariable String id) {
		return service.advance(id);
	}
}



