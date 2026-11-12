package com.duoc.gasVolcan.config;

import java.math.BigDecimal;

import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.model.Usuario;
import com.duoc.gasVolcan.repository.ProductoRepository;
import com.duoc.gasVolcan.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {

	@Bean
	CommandLineRunner seedDatabase(ProductoRepository productoRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepository.count() == 0) {
				Usuario admin = new Usuario();
				admin.setUsername("admin");
				admin.setEmail("admin@gasvolcan.cl");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole("ROLE_ADMIN");
				usuarioRepository.save(admin);

				Usuario cliente = new Usuario();
				cliente.setUsername("cliente");
				cliente.setEmail("cliente@gasvolcan.cl");
				cliente.setPassword(passwordEncoder.encode("cliente123"));
				cliente.setRole("ROLE_CLIENTE");
				usuarioRepository.save(cliente);
			}

			if (productoRepository.count() > 0) {
				return;
			}
			productoRepository.save(new Producto("Cilindro 5 kg", BigDecimal.valueOf(10000), null, "cilindro.svg", 50,
					"Cilindros"));
			productoRepository.save(new Producto("Cilindro 11 kg", BigDecimal.valueOf(15500), BigDecimal.valueOf(13990),
					"cilindro.svg", 120, "Cilindros"));
			productoRepository.save(new Producto("Cilindro 15 kg", BigDecimal.valueOf(21000), null, "cilindro.svg", 80,
					"Cilindros"));
			productoRepository.save(new Producto("Cilindro 45 kg", BigDecimal.valueOf(65000), null, "cilindro.svg", 12,
					"Cilindros"));
			productoRepository.save(new Producto("Regulador de gas", BigDecimal.valueOf(10990), BigDecimal.valueOf(8990),
					"regulador.svg", 35, "Accesorios"));
			productoRepository.save(new Producto("Flexible para gas", BigDecimal.valueOf(5990), null, "flexible.svg", 40,
					"Accesorios"));
		};
	}
}
