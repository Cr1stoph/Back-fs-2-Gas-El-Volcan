package com.duoc.gasVolcan.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Matriz de acceso exigida por la rúbrica:
 * - ADMINISTRADOR: acceso total.
 * - VENDEDOR: sólo consultas de productos y boletas (GET); sin escritura.
 * - CLIENTE: tienda pública, creación de pedidos y consulta de SUS pedidos.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomUserDetailsService userDetailsService;
	private final JwtAuthenticationEntryPoint authenticationEntryPoint;
	private final JwtAccessDeniedHandler accessDeniedHandler;

	public SecurityConfig(
			JwtAuthenticationFilter jwtAuthenticationFilter,
			CustomUserDetailsService userDetailsService,
			JwtAuthenticationEntryPoint authenticationEntryPoint,
			JwtAccessDeniedHandler accessDeniedHandler) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.userDetailsService = userDetailsService;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

				// Documentación y endpoints públicos de autenticación
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register",
						"/api/v1/auth/refresh").permitAll()
				.requestMatchers("/api/v1/auth/me").authenticated()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
				.requestMatchers("/h2-console/**").hasRole("ADMIN")

				// Catálogo: lectura pública, escritura exclusiva de ADMINISTRADOR
				.requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/*").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/productos", "/api/v1/productos/*").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/v1/productos/*").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/productos/*").hasRole("ADMIN")

				// Administración de USUARIO y CATEGORIA: nadie más que ADMIN
				.requestMatchers("/api/v1/usuarios", "/api/v1/usuarios/**").hasRole("ADMIN")
				.requestMatchers("/api/v1/categorias", "/api/v1/categorias/**").hasRole("ADMIN")

				// Pedidos: VENDEDOR sólo consulta; CLIENTE crea y consulta los suyos.
				.requestMatchers(HttpMethod.GET, "/api/v1/pedidos/mios").hasAnyRole("ADMIN", "VENDEDOR", "CLIENTE")
				.requestMatchers(HttpMethod.GET, "/api/v1/pedidos").hasAnyRole("ADMIN", "VENDEDOR")
				.requestMatchers(HttpMethod.GET, "/api/v1/pedidos/*").hasAnyRole("ADMIN", "VENDEDOR", "CLIENTE")
				.requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasAnyRole("ADMIN", "CLIENTE")
				.requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/*").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/*").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/pedidos/*").hasRole("ADMIN")

				// Cualquier ruta nueva debe declararse explícitamente.
				.anyRequest().denyAll()
			)
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
