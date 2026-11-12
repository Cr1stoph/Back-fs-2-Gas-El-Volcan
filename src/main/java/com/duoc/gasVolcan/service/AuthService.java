package com.duoc.gasVolcan.service;

import com.duoc.gasVolcan.model.AuthResponse;
import com.duoc.gasVolcan.model.LoginRequest;
import com.duoc.gasVolcan.model.RegisterRequest;
import com.duoc.gasVolcan.model.Usuario;
import com.duoc.gasVolcan.repository.UsuarioRepository;
import com.duoc.gasVolcan.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthService(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			AuthenticationManager authenticationManager) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	public AuthResponse register(RegisterRequest request) {
		if (usuarioRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
		}
		if (usuarioRepository.existsByUsername(request.username())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado");
		}

		String role = request.role() != null && !request.role().isBlank() ? request.role() : "ROLE_CLIENTE";
		if (!role.startsWith("ROLE_")) {
			role = "ROLE_" + role.toUpperCase();
		}

		Usuario usuario = new Usuario();
		usuario.setUsername(request.username());
		usuario.setEmail(request.email());
		usuario.setPassword(passwordEncoder.encode(request.password()));
		usuario.setRole(role);

		usuarioRepository.save(usuario);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username(usuario.getEmail())
				.password(usuario.getPassword())
				.authorities(usuario.getRole())
				.build();

		String token = jwtService.generateToken(userDetails);

		return new AuthResponse(token, usuario.getUsername(), usuario.getEmail(), usuario.getRole());
	}

	public AuthResponse login(LoginRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.email(), request.password())
			);

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String token = jwtService.generateToken(userDetails);

			Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

			return new AuthResponse(token, usuario.getUsername(), usuario.getEmail(), usuario.getRole());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
		}
	}
}
