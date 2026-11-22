package com.duoc.gasVolcan.repository;

import java.util.Optional;

import com.duoc.gasVolcan.model.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	Optional<Categoria> findByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCase(String nombre);
}
