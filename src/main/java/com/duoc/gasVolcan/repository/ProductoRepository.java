package com.duoc.gasVolcan.repository;

import java.util.List;

import com.duoc.gasVolcan.model.Producto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

	List<Producto> findByCategoriaIgnoreCaseOrderById(String categoria);

	boolean existsByCategoriaIgnoreCase(String categoria);
}
