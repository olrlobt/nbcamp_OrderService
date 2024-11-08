package com.nbcamp.orderservice.domain.user.dto;

import com.nbcamp.orderservice.domain.common.UserRole;

public record SignupRequest(
	String username,
	String password,
	UserRole userRole
) {
}
