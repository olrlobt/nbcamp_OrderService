package com.nbcamp.orderservice.domain.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.dto.UserUpdateRequest;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

public interface UserService {

	UserResponse signup(SignupRequest signupRequest);

	void logout(String username);

	UserResponse getUserDetail(String userId);

	Page<UserResponse> getAllUsers(SortOption sortOption, Pageable pageable);

	UserResponse updateUser(UserDetailsImpl userDetails, String userId, UserUpdateRequest request);

	void deleteUser(UserDetailsImpl userDetails, String userId);

	void updateRefreshToken(String username, String refreshToken);

	User findById(String userId);

	void updateUserRole(String userId, String role);
}
