package com.nbcamp.orderservice.domain.user.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.dto.UserUpdateRequest;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponse signup(SignupRequest signupRequest) {
		User user = User.create(signupRequest, passwordEncoder);
		userRepository.save(user);
		return UserResponse.of(user);
	}

	public void logout(String username) {
		jwtService.destroyRefreshToken(username);
	}

	public User findById(UUID userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
	}

	@Transactional(readOnly = true)
	public UserResponse getUserDetail(UUID userId) {
		return userRepository.findUserResponseByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
	}

	@Transactional(readOnly = true)
	public Page<UserResponse> getAllUsers(SortOption sortOption, Pageable pageable) {
		return userRepository.findAllUserResponse(sortOption, pageable);
	}

	@Transactional
	public UserResponse updateUser(UserDetailsImpl userDetails, UUID userId, UserUpdateRequest request) {
		ignoreAuth(userDetails, userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
		user.update(request, passwordEncoder);
		return UserResponse.of(user);
	}

	@Transactional
	public void deleteUser(UserDetailsImpl userDetails, UUID userId) {
		ignoreAuth(userDetails, userId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
		user.delete();
	}

	@Transactional
	public void updateRefreshToken(String username, String refreshToken) {
		userRepository.findByUsernameAndDeletedAtIsNull(username).ifPresent(
			users -> users.updateRefreshToken(refreshToken)
		);
	}

	@Transactional
	public void updateUserRole(UUID userId, String role) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));

		try {
			UserRole userRole = UserRole.valueOf(role.toUpperCase());
			user.updateRole(userRole);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ROLE.getMessage());
		}
	}

	private void ignoreAuth(UserDetailsImpl userDetails, UUID userId) {
		UserRole userRole = userDetails.getUserRole();
		if((userRole == UserRole.CUSTOMER || userRole == UserRole.OWNER)
			&& !Objects.equals(userDetails.getUserId(), userId)){
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSION.getMessage());
		}
	}
}
