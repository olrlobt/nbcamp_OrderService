package com.nbcamp.orderservice.global.security.filter;

import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JsonUserAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final ObjectMapper objectMapper;

	private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
		new AntPathRequestMatcher("api/v1/login", "POST"); //=>   /login 의 요청에, POST로 온 요청에 매칭

	public JsonUserAuthenticationFilter(ObjectMapper objectMapper) {
		super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		IOException, ServletException {
		if (request.getContentType() == null || !request.getContentType().equals("application/json")) {
			throw new AuthenticationServiceException(
				"Authentication Content-Type not supported: " + request.getContentType());
		}

		String messageBody = StreamUtils.copyToString(request.getInputStream(), UTF_8);
		Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
		String username = usernamePasswordMap.get("username");
		String password = usernamePasswordMap.get("password");

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
			password);

		return this.getAuthenticationManager().authenticate(authRequest);
	}
}