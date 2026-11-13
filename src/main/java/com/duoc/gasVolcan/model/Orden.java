package com.duoc.gasVolcan.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Orden {

	@Id
	private String id = UUID.randomUUID().toString();
	private Instant creadoEn = Instant.now();
	private String clienteNombre;
	private String clienteCorreo;
	private String clienteTelefono;
	private String direccion;
	private String tipoEntrega;
	private String formaPago;
	private BigDecimal subtotal;
	private BigDecimal despacho;
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	private EstadoPago estadoPago;

	@Enumerated(EnumType.STRING)
	private EstadoOrden estado;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrdenItem> items = new ArrayList<>();

	public Orden() {
	}

	public String getId() { return id; }
	public Instant getCreadoEn() { return creadoEn; }
	public String getClienteNombre() { return clienteNombre; }
	public String getClienteCorreo() { return clienteCorreo; }
	public String getClienteTelefono() { return clienteTelefono; }
	public String getDireccion() { return direccion; }
	public String getTipoEntrega() { return tipoEntrega; }
	public String getFormaPago() { return formaPago; }
	public BigDecimal getSubtotal() { return subtotal; }
	public BigDecimal getDespacho() { return despacho; }
	public BigDecimal getTotal() { return total; }
	public EstadoPago getEstadoPago() { return estadoPago; }
	public EstadoOrden getEstado() { return estado; }
	public List<OrdenItem> getItems() { return items; }

	public void setClienteNombre(String value) { clienteNombre = value; }
	public void setClienteCorreo(String value) { clienteCorreo = value; }
	public void setClienteTelefono(String value) { clienteTelefono = value; }
	public void setDireccion(String value) { direccion = value; }
	public void setTipoEntrega(String value) { tipoEntrega = value; }
	public void setFormaPago(String value) { formaPago = value; }
	public void setSubtotal(BigDecimal value) { subtotal = value; }
	public void setDespacho(BigDecimal value) { despacho = value; }
	public void setTotal(BigDecimal value) { total = value; }
	public void setEstadoPago(EstadoPago value) { estadoPago = value; }
	public void setEstado(EstadoOrden value) { estado = value; }
}



