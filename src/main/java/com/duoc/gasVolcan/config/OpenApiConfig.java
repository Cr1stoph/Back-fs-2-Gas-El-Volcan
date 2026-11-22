package com.duoc.gasVolcan.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger/OpenAPI con esquema bearer JWT y botón Authorize funcional. */
@Configuration
public class OpenApiConfig {

	public static final String SEGURIDAD_JWT = "bearerAuth";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("API Gas El Volcán")
						.version("v1")
						.description("API REST versionada (/api/v1) para tienda, autenticación y gestión de pedidos"))
				.components(new Components().addSecuritySchemes(SEGURIDAD_JWT,
						new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.in(SecurityScheme.In.HEADER)
								.name("Authorization")))
				.addSecurityItem(new SecurityRequirement().addList(SEGURIDAD_JWT));
	}
}
