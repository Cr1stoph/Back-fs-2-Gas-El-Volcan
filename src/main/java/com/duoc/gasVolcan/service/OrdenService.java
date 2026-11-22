package com.duoc.gasVolcan.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.duoc.gasVolcan.model.EstadoOrden;
import com.duoc.gasVolcan.model.EstadoPago;
import com.duoc.gasVolcan.model.Orden;
import com.duoc.gasVolcan.model.OrdenItem;
import com.duoc.gasVolcan.model.OrdenItemRequest;
import com.duoc.gasVolcan.model.OrdenRequest;
import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.repository.OrdenRepository;
import com.duoc.gasVolcan.repository.ProductoRepository;
import com.duoc.gasVolcan.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * BOLETA / DETALLE_BOLETA: cálculo de subtotal, despacho y total, control de stock,
 * validaciones de entrega/pago y propiedad de las boletas de cada CLIENTE.
 */
@Service
public class OrdenService {

	private static final BigDecimal SHIPPING_COST = BigDecimal.valueOf(2500);
	private static final String DECLINED_CARD = "0000000000000000";

	private final OrdenRepository orderRepository;
	private final ProductoRepository productoRepositorio;
	private final UsuarioRepository usuarioRepository;

	public OrdenService(OrdenRepository orderRepository, ProductoRepository productoRepositorio,
			UsuarioRepository usuarioRepository) {
		this.orderRepository = orderRepository;
		this.productoRepositorio = productoRepositorio;
		this.usuarioRepository = usuarioRepository;
	}

	public List<Orden> findAll() {
		return orderRepository.findAll();
	}

	/** Pedidos propios del CLIENTE autenticado. */
	public List<Orden> findByCliente(String email) {
		return orderRepository.findByClienteCorreoIgnoreCaseOrderByCreadoEnDesc(email);
	}

	public Orden findById(String id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
	}

	/** Un CLIENTE sólo puede ver sus propias boletas; ADMIN y VENDEDOR ven todas. */
	public Orden findByIdParaCliente(String id, String emailCliente) {
		Orden orden = findById(id);
		if (emailCliente == null
				|| (!emailCliente.equalsIgnoreCase(orden.getClienteCorreo())
						&& !orden.perteneceA(emailCliente))) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"No tienes permiso para ver este pedido");
		}
		return orden;
	}

	@Transactional
	public Orden create(OrdenRequest request) {
		return create(request, null);
	}

	@Transactional
	public Orden create(OrdenRequest request, String emailUsuario) {
		validateDelivery(request);
		Map<Long, Integer> cantidades = cantidadesAgregadas(request.items());

		Orden orden = new Orden();
		aplicarDatosCliente(orden, request);
		if (emailUsuario != null) {
			usuarioRepository.findByEmail(emailUsuario).ifPresent(orden::setUsuario);
		}
		construirItems(orden, cantidades);
		boolean aprobado = pagoAprobado(request);

		if (aprobado) {
			ajustarStock(cantidades, -1);
		}
		cerrarMontos(orden, request, aprobado);
		return orderRepository.save(orden);
	}

	@Transactional
	public Orden update(String id, OrdenRequest request) {
		Orden orden = findById(id);
		if (orden.getEstadoPago() != EstadoPago.APROBADO || orden.getEstado() != EstadoOrden.PENDIENTE) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Sólo se pueden editar pedidos aprobados que estén pendientes");
		}
		validateDelivery(request);
		Map<Long, Integer> cantidadesAnteriores = cantidadesDeItems(orden.getItems());
		Map<Long, Integer> cantidadesNuevas = cantidadesAgregadas(request.items());

		ajustarStock(cantidadesAnteriores, +1);
		ajustarStock(cantidadesNuevas, -1);

		aplicarDatosCliente(orden, request);
		orden.getItems().clear();
		construirItems(orden, cantidadesNuevas);
		cerrarMontos(orden, request, true);
		return orden;
	}

	@Transactional
	public void delete(String id) {
		Orden orden = findById(id);
		if (orden.getEstadoPago() == EstadoPago.APROBADO) {
			ajustarStock(cantidadesDeItems(orden.getItems()), +1);
		}
		orderRepository.delete(orden);
	}

	@Transactional
	public Orden advance(String id) {
		Orden orden = findById(id);
		if (orden.getEstadoPago() != EstadoPago.APROBADO) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden avanzar pagos aprobados");
		}
		orden.setEstado(switch (orden.getEstado()) {
			case PENDIENTE -> EstadoOrden.EN_CAMINO;
			case EN_CAMINO -> EstadoOrden.ENTREGADO;
			case ENTREGADO -> EstadoOrden.ENTREGADO;
			case PAGO_RECHAZADO -> throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Un pedido rechazado no puede avanzar");
		});
		return orden;
	}

	private void aplicarDatosCliente(Orden orden, OrdenRequest request) {
		orden.setClienteNombre(request.nombre().trim());
		orden.setClienteCorreo(request.correo().trim().toLowerCase());
		orden.setClienteTelefono(request.telefono().trim());
		orden.setDireccion("retiro".equals(request.tipoEntrega())
				? "Retiro en tienda" : request.direccion().trim());
		orden.setTipoEntrega(request.tipoEntrega());
		orden.setFormaPago(request.formaPago());
	}

	private void construirItems(Orden orden, Map<Long, Integer> cantidades) {
		for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
			Producto producto = productoRepositorio.findById(entrada.getKey())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
			OrdenItem item = new OrdenItem(producto.getId(), producto.getNombre(),
					precioUnitario(producto), entrada.getValue());
			item.setProducto(producto);
			item.setOrden(orden);
			orden.getItems().add(item);
		}
	}

	private void cerrarMontos(Orden orden, OrdenRequest request, boolean aprobado) {
		BigDecimal subtotal = BigDecimal.ZERO;
		for (OrdenItem item : orden.getItems()) {
			subtotal = subtotal.add(item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
		}
		BigDecimal despacho = "despacho".equals(request.tipoEntrega()) ? SHIPPING_COST : BigDecimal.ZERO;
		orden.setSubtotal(subtotal);
		orden.setDespacho(despacho);
		orden.setTotal(subtotal.add(despacho));
		orden.setEstadoPago(aprobado ? EstadoPago.APROBADO : EstadoPago.RECHAZADO);
		orden.setEstado(aprobado ? EstadoOrden.PENDIENTE : EstadoOrden.PAGO_RECHAZADO);
	}

	private boolean pagoAprobado(OrdenRequest request) {
		return !"tarjeta".equals(request.formaPago()) || !DECLINED_CARD.equals(request.numeroTarjeta());
	}

	/** Suma cantidades repetidas del mismo producto para no sobre-ventilar el stock. */
	private Map<Long, Integer> cantidadesAgregadas(List<OrdenItemRequest> items) {
		Map<Long, Integer> cantidades = new LinkedHashMap<>();
		for (OrdenItemRequest item : items) {
			cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
		}
		return cantidades;
	}

	private Map<Long, Integer> cantidadesDeItems(List<OrdenItem> items) {
		Map<Long, Integer> cantidades = new LinkedHashMap<>();
		for (OrdenItem item : items) {
			cantidades.merge(item.getProductoId(), item.getCantidad(), Integer::sum);
		}
		return cantidades;
	}

	private void ajustarStock(Map<Long, Integer> cantidades, int signo) {
		for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
			Producto producto = productoRepositorio.findById(entrada.getKey())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
			int nuevoStock = producto.getStock() + signo * entrada.getValue();
			if (nuevoStock < 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Stock insuficiente para " + producto.getNombre());
			}
			producto.setStock(nuevoStock);
		}
	}

	private BigDecimal precioUnitario(Producto producto) {
		return producto.getPrecioOferta() != null
				&& producto.getPrecioOferta().compareTo(producto.getPrecio()) < 0
						? producto.getPrecioOferta()
						: producto.getPrecio();
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
		if (request.items() == null || request.items().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido debe tener al menos un producto");
		}
	}
}
