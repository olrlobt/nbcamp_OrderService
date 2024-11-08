package com.nbcamp.orderservice.domain.user.dto;

public record LoginResponse (
	String username,
	String accessToken
) {
}
