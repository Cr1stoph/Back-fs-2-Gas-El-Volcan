package com.duoc.gasVolcan.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrdenItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long productoId;
	private String nombre;
	private BigDecimal precio;
	private int cantidad;

	/** DETALLE_BOLETA → PRODUCTO (FK real, el snapshot de nombre/precio se conserva). */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id_rel")
	private Producto producto;

	/** Dueño de la relación @OneToMany(mappedBy = "orden") de Orden. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orden_id")
	private Orden orden;

	protected OrdenItem() {
	}

	public OrdenItem(Long productoId, String nombre, BigDecimal precio, int cantidad) {
		this.productoId = productoId;
		this.nombre = nombre;
		this.precio = precio;
		this.cantidad = cantidad;
	}

	public Long getId() { return id; }
	public Long getProductoId() { return productoId; }
	public String getNombre() { return nombre; }
	public BigDecimal getPrecio() { return precio; }
	public int getCantidad() { return cantidad; }

	/** Sin getters públicos: la API expone productoId, no las entidades relacionadas. */
	public void setProducto(Producto value) { producto = value; }
	public void setOrden(Orden value) { orden = value; }
}



