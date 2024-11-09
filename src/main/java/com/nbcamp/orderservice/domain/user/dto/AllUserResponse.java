package com.nbcamp.orderservice.domain.user.dto;

import java.util.List;

public record AllUserResponse(
	List<UserResponse> userResponses
) {
}
