package com.duoc.gasVolcan.service;

import java.util.List;

import com.duoc.gasVolcan.model.Rol;
import com.duoc.gasVolcan.model.Usuario;
import com.duoc.gasVolcan.model.UsuarioRequest;
import com.duoc.gasVolcan.model.UsuarioResponse;
import com.duoc.gasVolcan.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Lógica de negocio de USUARIO: validación de roles, unicidad y passwords cifrados. */
@Service
public class UsuarioService {

	private final UsuarioRepository repository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<Usuario> findAll() {
		return repository.findAll();
	}

	public Usuario findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
	}

	public UsuarioResponse currentUser(String email) {
		Usuario usuario = repository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
		return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getEmail(), usuario.getRole());
	}

	@Transactional
	public Usuario create(UsuarioRequest request) {
		String email = normalizarEmail(request.email());
		if (repository.existsByEmail(email) || repository.existsByUsername(request.username().trim())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email o nombre de usuario ya está registrado");
		}
		if (request.password() == null || request.password().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
		}
		Usuario usuario = new Usuario();
		usuario.setUsername(request.username().trim());
		usuario.setEmail(email);
		usuario.setPassword(passwordEncoder.encode(request.password()));
		usuario.setRole(resolverRol(request.role()));
		return repository.save(usuario);
	}

	@Transactional
	public Usuario update(Long id, UsuarioRequest request) {
		Usuario usuario = findById(id);
		String email = normalizarEmail(request.email());
		repository.findByEmail(email)
				.filter(otro -> !otro.getId().equals(id))
				.ifPresent(otro -> {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
				});
		repository.findByUsername(request.username().trim())
				.filter(otro -> !otro.getId().equals(id))
				.ifPresent(otro -> {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado");
				});

		usuario.setUsername(request.username().trim());
		usuario.setEmail(email);
		if (request.password() != null && !request.password().isBlank()) {
			usuario.setPassword(passwordEncoder.encode(request.password()));
		}
		usuario.setRole(resolverRol(request.role()));
		return usuario;
	}

	@Transactional
	public void delete(Long id) {
		repository.delete(findById(id));
	}

	private String normalizarEmail(String email) {
		return email == null ? null : email.trim().toLowerCase();
	}

	private String resolverRol(String rol) {
		String normalizado = Rol.normalizar(rol);
		if (normalizado == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Rol inválido. Use ADMIN, VENDEDOR o CLIENTE");
		}
		return normalizado;
	}
}
