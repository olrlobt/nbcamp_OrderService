package com.nbcamp.orderservice.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import com.nbcamp.orderservice.domain.user.dto.UserResponse;

public interface UserQueryRepository {
	Optional<UserResponse> findUserResponseByUserId(UUID userId);
}
