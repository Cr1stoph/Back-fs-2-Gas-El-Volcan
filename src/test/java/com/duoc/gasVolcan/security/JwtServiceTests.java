package com.duoc.gasVolcan.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTests {

	private final JwtService jwtService = new JwtService(
			"UnaClaveJWTDePruebaConAlMenosTreintaYDosCaracteresSeguros",
			60000, true);

	@Test
	void generaYRenuevaUnTokenParaElUsuario() {
		UserDetails usuario = new User(
				"vendedor@gasvolcan.cl",
				"no-se-usa",
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_VENDEDOR")));

		String token = jwtService.generateToken(usuario);

		assertEquals("vendedor@gasvolcan.cl", jwtService.extractUsername(token));
		assertEquals("ROLE_VENDEDOR", jwtService.extractRole(token));
		assertTrue(jwtService.isTokenValid(token, usuario));

		String tokenRenovado = jwtService.generateToken(usuario);
		assertTrue(jwtService.isTokenValid(tokenRenovado, usuario));
		assertFalse(token.equals(tokenRenovado));
	}
}
