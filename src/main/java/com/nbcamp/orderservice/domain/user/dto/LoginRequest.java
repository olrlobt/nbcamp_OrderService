package com.nbcamp.orderservice.domain.user.dto;

public record LoginRequest(
	String username,
	String password
) {
}
