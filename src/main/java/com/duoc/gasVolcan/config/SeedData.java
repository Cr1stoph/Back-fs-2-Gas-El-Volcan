package com.duoc.gasVolcan.config;

import java.math.BigDecimal;

import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.model.Usuario;
import com.duoc.gasVolcan.repository.ProductoRepository;
import com.duoc.gasVolcan.repository.UsuarioRepository;
import com.duoc.gasVolcan.service.CategoriaService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Datos iniciales de demostración (credenciales documentadas en README). */
@Configuration
public class SeedData {

	@Bean
	CommandLineRunner seedDatabase(ProductoRepository productoRepository, UsuarioRepository usuarioRepository,
			CategoriaService categoriaService, PasswordEncoder passwordEncoder) {
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

				Usuario vendedor = new Usuario();
				vendedor.setUsername("vendedor");
				vendedor.setEmail("vendedor@gasvolcan.cl");
				vendedor.setPassword(passwordEncoder.encode("vendedor123"));
				vendedor.setRole("ROLE_VENDEDOR");
				usuarioRepository.save(vendedor);
			}

			var cilindros = categoriaService.findOrCreate("Cilindros");
			var accesorios = categoriaService.findOrCreate("Accesorios");

			if (productoRepository.count() > 0) {
				return;
			}
			guardar(productoRepository, "Cilindro 5 kg", "10000", null, "cilindro.svg", 50, "Cilindros", cilindros);
			guardar(productoRepository, "Cilindro 11 kg", "15500", "13990", "cilindro.svg", 120, "Cilindros", cilindros);
			guardar(productoRepository, "Cilindro 15 kg", "21000", null, "cilindro.svg", 80, "Cilindros", cilindros);
			guardar(productoRepository, "Cilindro 45 kg", "65000", null, "cilindro.svg", 12, "Cilindros", cilindros);
			guardar(productoRepository, "Regulador de gas", "10990", "8990", "regulador.svg", 35, "Accesorios", accesorios);
			guardar(productoRepository, "Flexible para gas", "5990", null, "flexible.svg", 40, "Accesorios", accesorios);
		};
	}

	private void guardar(ProductoRepository repository, String nombre, String precio, String oferta,
			String img, int stock, String categoria, com.duoc.gasVolcan.model.Categoria categoriaRef) {
		Producto producto = new Producto(nombre, new BigDecimal(precio),
				oferta == null ? null : new BigDecimal(oferta), img, stock, categoria);
		producto.setCategoriaRef(categoriaRef);
		repository.save(producto);
	}
}
