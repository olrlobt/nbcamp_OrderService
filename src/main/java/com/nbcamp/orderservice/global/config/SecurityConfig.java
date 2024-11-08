package com.nbcamp.orderservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;
import com.nbcamp.orderservice.domain.user.service.JwtService;
import com.nbcamp.orderservice.global.security.filter.JsonUserAuthenticationFilter;
import com.nbcamp.orderservice.global.security.filter.JwtAuthenticationFilter;
import com.nbcamp.orderservice.global.security.handler.LoginFailureHandler;
import com.nbcamp.orderservice.global.security.handler.LoginSuccessJWTProvideHandler;
import com.nbcamp.orderservice.global.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;
	private final UserRepository usersRepository;
	private final JwtService jwtService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/api/v1/users/login", "/api/v1/users/signup").permitAll()
				.anyRequest().authenticated())
			.logout((logout) -> logout
				.invalidateHttpSession(true))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
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
			new LoginSuccessJWTProvideHandler(jwtService, usersRepository));
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