package com.duoc.gasVolcan.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}



