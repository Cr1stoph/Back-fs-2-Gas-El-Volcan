package com.duoc.gasVolcan.security;

import java.util.Collections;

import com.duoc.gasVolcan.model.Usuario;
import com.duoc.gasVolcan.repository.UsuarioRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmail(identifier)
				.or(() -> usuarioRepository.findByUsername(identifier))
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email o username: " + identifier));

		return new User(
				usuario.getEmail(), // We use email as the principal username for lookup/jwt subject
				usuario.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority(usuario.getRole()))
		);
	}
}
