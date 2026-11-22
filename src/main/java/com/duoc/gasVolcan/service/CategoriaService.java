package com.duoc.gasVolcan.service;

import java.util.List;

import com.duoc.gasVolcan.model.Categoria;
import com.duoc.gasVolcan.model.CategoriaRequest;
import com.duoc.gasVolcan.repository.CategoriaRepository;
import com.duoc.gasVolcan.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoriaService {

	private final CategoriaRepository repository;
	private final ProductoRepository productoRepository;

	public CategoriaService(CategoriaRepository repository, ProductoRepository productoRepository) {
		this.repository = repository;
		this.productoRepository = productoRepository;
	}

	public List<Categoria> findAll() {
		return repository.findAll();
	}

	public Categoria findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
	}

	/** Reutiliza la categoría por nombre (case-insensitive) o la crea al cargar productos. */
	@Transactional
	public Categoria findOrCreate(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría es obligatoria");
		}
		String valor = nombre.trim();
		return repository.findByNombreIgnoreCase(valor)
				.orElseGet(() -> repository.save(new Categoria(valor, null)));
	}

	@Transactional
	public Categoria create(CategoriaRequest request) {
		String nombre = request.nombre().trim();
		if (repository.existsByNombreIgnoreCase(nombre)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría ya existe");
		}
		return repository.save(new Categoria(nombre, request.descripcion()));
	}

	@Transactional
	public Categoria update(Long id, CategoriaRequest request) {
		Categoria categoria = findById(id);
		String nombre = request.nombre().trim();
		repository.findByNombreIgnoreCase(nombre)
				.filter(otra -> !otra.getId().equals(id))
				.ifPresent(otra -> {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría ya existe");
				});
		categoria.setNombre(nombre);
		categoria.setDescripcion(request.descripcion());
		return categoria;
	}

	@Transactional
	public void delete(Long id) {
		Categoria categoria = findById(id);
		if (productoRepository.existsByCategoriaIgnoreCase(categoria.getNombre())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar una categoría con productos asociados");
		}
		repository.delete(categoria);
	}
}
