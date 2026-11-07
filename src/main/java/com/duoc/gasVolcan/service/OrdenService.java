package com.duoc.gasVolcan.service;

import java.math.BigDecimal;
import java.util.List;

import com.duoc.gasVolcan.model.Orden;
import com.duoc.gasVolcan.model.OrdenItem;
import com.duoc.gasVolcan.model.OrdenItemRequest;
import com.duoc.gasVolcan.model.OrdenRequest;
import com.duoc.gasVolcan.model.EstadoOrden;
import com.duoc.gasVolcan.model.EstadoPago;
import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.repository.OrdenRepository;
import com.duoc.gasVolcan.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdenService {

	private static final BigDecimal SHIPPING_COST = BigDecimal.valueOf(2500);
	private static final String DECLINED_CARD = "0000000000000000";

	private final OrdenRepository orderRepository;
	private final ProductoRepository productoRepositorio;

	public OrdenService(OrdenRepository orderRepository, ProductoRepository productoRepositorio) {
		this.orderRepository = orderRepository;
		this.productoRepositorio = productoRepositorio;
	}

	public List<Orden> findAll() {
		return orderRepository.findAll();
	}

	public Orden findById(String id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
	}

	@Transactional
	public Orden create(OrdenRequest request) {
		validateDelivery(request);
		Orden order = new Orden();
		order.setClienteNombre(request.nombre().trim());
		order.setClienteCorreo(request.correo().trim().toLowerCase());
		order.setClienteTelefono(request.telefono().trim());
		order.setDireccion(request.tipoEntrega().equals("retiro") ? "Retiro en tienda" : request.direccion().trim());
		order.setTipoEntrega(request.tipoEntrega());
		order.setFormaPago(request.formaPago());
		boolean approved = !"tarjeta".equals(request.formaPago())
				|| !DECLINED_CARD.equals(request.numeroTarjeta());

		BigDecimal subtotal = BigDecimal.ZERO;
		for (OrdenItemRequest itemRequest : request.items()) {
			Producto producto = productoRepositorio.findById(itemRequest.productoId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
			if (producto.getStock() < itemRequest.cantidad()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Stock insuficiente para " + producto.getNombre());
			}
			BigDecimal unitPrice = producto.getPrecioOferta() != null
					&& producto.getPrecioOferta().compareTo(producto.getPrecio()) < 0
							? producto.getPrecioOferta()
							: producto.getPrecio();
			subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(itemRequest.cantidad())));
			if (approved) {
				producto.setStock(producto.getStock() - itemRequest.cantidad());
			}
			order.getItems().add(new OrdenItem(producto.getId(), producto.getNombre(), unitPrice,
					itemRequest.cantidad()));
		}

		BigDecimal shipping = request.tipoEntrega().equals("despacho") ? SHIPPING_COST : BigDecimal.ZERO;
		order.setSubtotal(subtotal);
		order.setDespacho(shipping);
		order.setTotal(subtotal.add(shipping));
		order.setEstadoPago(approved ? EstadoPago.APROBADO : EstadoPago.RECHAZADO);
		order.setEstado(approved ? EstadoOrden.PENDIENTE : EstadoOrden.PAGO_RECHAZADO);
		return orderRepository.save(order);
	}

	@Transactional
	public Orden advance(String id) {
		Orden order = findById(id);
		if (order.getEstadoPago() != EstadoPago.APROBADO) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden avanzar pagos aprobados");
		}
		order.setEstado(switch (order.getEstado()) {
			case PENDIENTE -> EstadoOrden.EN_CAMINO;
			case EN_CAMINO -> EstadoOrden.ENTREGADO;
			case ENTREGADO -> EstadoOrden.ENTREGADO;
			case PAGO_RECHAZADO -> throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Un pedido rechazado no puede avanzar");
		});
		return order;
	}

	private void validateDelivery(OrdenRequest request) {
		if (!"despacho".equals(request.tipoEntrega()) && !"retiro".equals(request.tipoEntrega())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de entrega inválido");
		}
		if (!"tarjeta".equals(request.formaPago()) && !"contraentrega".equals(request.formaPago())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Forma de pago inválida");
		}
		if ("despacho".equals(request.tipoEntrega()) && (request.direccion() == null || request.direccion().isBlank())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La dirección es obligatoria para despacho");
		}
		if ("tarjeta".equals(request.formaPago())
				&& (request.numeroTarjeta() == null || !request.numeroTarjeta().matches("\\d{16}"))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La tarjeta debe tener 16 dígitos");
		}
	}
}


