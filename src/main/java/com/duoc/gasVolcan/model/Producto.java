package com.duoc.gasVolcan.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal precio;

	@Column(precision = 12, scale = 2)
	private BigDecimal precioOferta;

	@Column(length = 500)
	private String img;

	@Column(nullable = false)
	private int stock;

	@Column(nullable = false, length = 60)
	private String categoria;

	protected Producto() {
	}

	public Producto(String nombre, BigDecimal precio, BigDecimal precioOferta, String img, int stock, String categoria) {
		this.nombre = nombre;
		this.precio = precio;
		this.precioOferta = precioOferta;
		this.img = img;
		this.stock = stock;
		this.categoria = categoria;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public BigDecimal getPrecioOferta() {
		return precioOferta;
	}

	public void setPrecioOferta(BigDecimal precioOferta) {
		this.precioOferta = precioOferta;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
}



