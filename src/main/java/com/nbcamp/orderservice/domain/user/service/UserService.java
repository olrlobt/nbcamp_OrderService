package com.nbcamp.orderservice.domain.user.service;

import com.nbcamp.orderservice.domain.user.dto.AllUserResponse;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.dto.UserUpdateRequest;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

public interface UserService {

	UserResponse signup(SignupRequest signupRequest);

	void logout(String username);

	UserResponse getUserDetail(String userId);

	AllUserResponse getAllUsers();

	UserResponse updateUser(UserDetailsImpl userDetails, String userId, UserUpdateRequest request);

	void deleteUser(UserDetailsImpl userDetails, String userId);

	void updateRefreshToken(String username, String refreshToken);

	public User findById(String userId);
}
