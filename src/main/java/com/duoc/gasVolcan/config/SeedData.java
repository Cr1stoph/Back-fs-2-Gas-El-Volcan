package com.duoc.gasVolcan.config;

import java.math.BigDecimal;

import com.duoc.gasVolcan.model.Producto;
import com.duoc.gasVolcan.repository.ProductoRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedData {

	@Bean
	CommandLineRunner seedProductos(ProductoRepository repository) {
		return args -> {
			if (repository.count() > 0) {
				return;
			}
			repository.save(new Producto("Cilindro 5 kg", BigDecimal.valueOf(10000), null, "cilindro.svg", 50,
					"Cilindros"));
			repository.save(new Producto("Cilindro 11 kg", BigDecimal.valueOf(15500), BigDecimal.valueOf(13990),
					"cilindro.svg", 120, "Cilindros"));
			repository.save(new Producto("Cilindro 15 kg", BigDecimal.valueOf(21000), null, "cilindro.svg", 80,
					"Cilindros"));
			repository.save(new Producto("Cilindro 45 kg", BigDecimal.valueOf(65000), null, "cilindro.svg", 12,
					"Cilindros"));
			repository.save(new Producto("Regulador de gas", BigDecimal.valueOf(10990), BigDecimal.valueOf(8990),
					"regulador.svg", 35, "Accesorios"));
			repository.save(new Producto("Flexible para gas", BigDecimal.valueOf(5990), null, "flexible.svg", 40,
					"Accesorios"));
		};
	}
}



