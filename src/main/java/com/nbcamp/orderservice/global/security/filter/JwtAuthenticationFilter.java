package com.nbcamp.orderservice.global.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;
import com.nbcamp.orderservice.domain.user.service.JwtService;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository usersRepository;
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	/**
	 * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
	 * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		if (request.getRequestURI().equals("/api/v1/users/login")) {
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = jwtService
			.extractRefreshToken(request)
			.filter(jwtService::isTokenValid)
			.orElse(null);

		if (refreshToken != null) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}
		checkAccessTokenAndAuthentication(request, response, filterChain);
	}

	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		jwtService.extractAccessToken(request)
			.filter(jwtService::isTokenValid)
			.flatMap(accessToken -> jwtService.extractUsername(accessToken)
				.flatMap(usersRepository::findByUsername))
			.ifPresent(this::saveAuthentication);
		filterChain.doFilter(request, response);
	}

	private void saveAuthentication(User users) {
		UserDetailsImpl userDetails = new UserDetailsImpl(users);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
			authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

		SecurityContext context = SecurityContextHolder.createEmptyContext();//5
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		usersRepository.findByRefreshToken(refreshToken).ifPresent(
			users -> jwtService.sendAccessToken(response, jwtService.createAccessToken(users.getRefreshToken()))
		);
	}
}
