package com.duoc.gasVolcan.service;

import java.math.BigDecimal;
import java.util.List;

import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.model.ProductoRequest;
import com.duoc.gasVolcan.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductoService {

	private final ProductoRepository repository;
	private final CategoriaService categoriaService;

	public ProductoService(ProductoRepository repository, CategoriaService categoriaService) {
		this.repository = repository;
		this.categoriaService = categoriaService;
	}

	public List<Producto> findAll() {
		return repository.findAll();
	}

	public List<Producto> findByCategoria(String categoria) {
		return repository.findByCategoriaIgnoreCaseOrderById(categoria);
	}

	public Producto findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
	}

	@Transactional
	public Producto create(ProductoRequest request) {
		validateOffer(request.precio(), request.precioOferta());
		Producto producto = new Producto(request.nombre().trim(), request.precio(), request.precioOferta(),
				request.img(), request.stock(), request.categoria().trim());
		producto.setCategoriaRef(categoriaService.findOrCreate(request.categoria()));
		return repository.save(producto);
	}

	@Transactional
	public Producto update(Long id, ProductoRequest request) {
		validateOffer(request.precio(), request.precioOferta());
		Producto producto = findById(id);
		producto.setNombre(request.nombre().trim());
		producto.setPrecio(request.precio());
		producto.setPrecioOferta(request.precioOferta());
		producto.setImg(request.img());
		producto.setStock(request.stock());
		producto.setCategoria(request.categoria().trim());
		producto.setCategoriaRef(categoriaService.findOrCreate(request.categoria()));
		return producto;
	}

	@Transactional
	public void delete(Long id) {
		Producto producto = findById(id);
		repository.delete(producto);
	}

	private void validateOffer(BigDecimal price, BigDecimal offer) {
		if (offer != null && offer.compareTo(price) >= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El precio de oferta debe ser menor al precio normal");
		}
	}
}
