package com.duoc.gasVolcan.repository;

import java.util.List;

import com.duoc.gasVolcan.model.Orden;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, String> {

	/** Permite a un CLIENTE consultar únicamente sus propias boletas/pedidos. */
	List<Orden> findByClienteCorreoIgnoreCaseOrderByCreadoEnDesc(String clienteCorreo);

	List<Orden> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
