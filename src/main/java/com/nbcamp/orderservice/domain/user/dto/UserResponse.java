package com.nbcamp.orderservice.domain.user.dto;

import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.user.entity.User;

public record UserResponse(
	String username,
	UserRole userRole
) {
	public static UserResponse of(User user) {
		return new UserResponse(
			user.getUsername(),
			user.getUserRole()
		);
	}
}
