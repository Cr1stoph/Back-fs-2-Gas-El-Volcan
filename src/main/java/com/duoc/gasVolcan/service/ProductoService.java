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

	public ProductoService(ProductoRepository repository) {
		this.repository = repository;
	}

	public List<Producto> findAll() {
		return repository.findAll();
	}

	public Producto findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
	}

	@Transactional
	public Producto create(ProductoRequest request) {
		validateOffer(request.precio(), request.precioOferta());
		return repository.save(new Producto(request.nombre().trim(), request.precio(), request.precioOferta(),
				request.img(), request.stock(), request.categoria().trim()));
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


