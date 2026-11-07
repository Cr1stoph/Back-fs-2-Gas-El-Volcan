package com.duoc.gasVolcan.controller;

import java.util.List;

import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.model.ProductoRequest;
import com.duoc.gasVolcan.service.ProductoService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/productos")
public class ProductoController {

	private final ProductoService service;

	public ProductoController(ProductoService service) {
		this.service = service;
	}

	@GetMapping
	public List<Producto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public Producto findById(@PathVariable Long id) {
		return service.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Producto create(@Valid @RequestBody ProductoRequest request) {
		return service.create(request);
	}

	@PutMapping("/{id}")
	public Producto update(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
		return service.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}
}



