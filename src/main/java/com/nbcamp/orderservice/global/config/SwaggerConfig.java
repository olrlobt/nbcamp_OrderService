package com.nbcamp.orderservice.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@OpenAPIDefinition(
	servers = {
		@Server(url = "http://3.37.116.58", description = "Server"),
		@Server(url = "http://localhost:8080", description = "Local")
	}
)
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("v1")
			.pathsToMatch("/**")
			.build();
	}

	@Bean
	public OpenAPI springShopOpenAPI() {
		Components components = new Components()
			.addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
				.name(HttpHeaders.AUTHORIZATION)
				.type(SecurityScheme.Type.APIKEY)
				.in(SecurityScheme.In.HEADER)
				.bearerFormat("JWT"));

		return new OpenAPI()
			.info(new Info()
				.title("12조")
				.description("AI 검증 비즈니스 프로젝트 REST API")
				.version("v1"))
			.addSecurityItem(new SecurityRequirement()
				.addList(HttpHeaders.AUTHORIZATION))
			.components(components);
	}

}