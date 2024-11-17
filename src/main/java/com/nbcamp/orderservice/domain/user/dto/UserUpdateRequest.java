package com.nbcamp.orderservice.domain.user.dto;

public record UserUpdateRequest(
	String username,
	String password
) {
}
