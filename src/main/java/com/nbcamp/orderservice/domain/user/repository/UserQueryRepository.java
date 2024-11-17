package com.nbcamp.orderservice.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;

public interface UserQueryRepository {
	Optional<UserResponse> findUserResponseByUserId(UUID userId);
	Page<UserResponse> findAllUserResponse(SortOption sortOption, Pageable pageable);
}
