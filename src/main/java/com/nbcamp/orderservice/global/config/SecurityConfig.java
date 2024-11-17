package com.nbcamp.orderservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;
import com.nbcamp.orderservice.domain.user.service.JwtService;
import com.nbcamp.orderservice.domain.user.service.UserService;
import com.nbcamp.orderservice.global.security.UserDetailsServiceImpl;
import com.nbcamp.orderservice.global.security.filter.JsonUserAuthenticationFilter;
import com.nbcamp.orderservice.global.security.filter.JwtAuthenticationFilter;
import com.nbcamp.orderservice.global.security.handler.LoginFailureHandler;
import com.nbcamp.orderservice.global.security.handler.LoginSuccessJWTProvideHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;
	private final UserRepository usersRepository;
	private final UserService userService;
	private final JwtService jwtService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.GET, "/api/v1/category/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/stores").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/orders/reviews").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/reviews/users/{userId}").permitAll()
				.requestMatchers("/api/v1/users/login", "/api/v1/users/signup", "/h2/**").permitAll()
				.anyRequest().authenticated())
			.logout((logout) -> logout
				.invalidateHttpSession(true))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);

		// XFrameOptionsHeaderWriter를 사용하여 SAMEORIGIN으로 설정
		http.headers(headers -> headers
			.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));

		http
			.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
			.addFilterBefore(jwtAuthenticationFilter(), JsonUserAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public JsonUserAuthenticationFilter jsonUsernamePasswordLoginFilter() {
		JsonUserAuthenticationFilter jsonUserLoginFilter = new JsonUserAuthenticationFilter(
			objectMapper);
		jsonUserLoginFilter.setAuthenticationManager(new ProviderManager(daoAuthenticationProvider()));
		jsonUserLoginFilter.setAuthenticationSuccessHandler(
			new LoginSuccessJWTProvideHandler(jwtService, userService));
		jsonUserLoginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());
		return jsonUserLoginFilter;
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtService, usersRepository);
	}
}
