package com.nbcamp.orderservice.domain.user.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.user.dto.AllUserResponse;
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

	@Transactional(readOnly = true)
	public UserResponse getUserDetail(String userId) {
		return userRepository.findUserResponseByUserId(UUID.fromString(userId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
	}

	@Transactional(readOnly = true)
	public AllUserResponse getAllUsers() {
		return userRepository.findAllUserResponse();
	}

	@Transactional
	public UserResponse updateUser(UserDetailsImpl userDetails, String userId, UserUpdateRequest request) {
		ignoreAuth(userDetails, userId);

		User user = userRepository.findById(UUID.fromString(userId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
		user.update(request);
		return UserResponse.of(user);
	}

	@Transactional
	public void deleteUser(UserDetailsImpl userDetails, String userId) {
		ignoreAuth(userDetails, userId);
		User user = userRepository.findById(UUID.fromString(userId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
		user.delete();
	}

	@Transactional
	public void updateRefreshToken(String username, String refreshToken) {
		userRepository.findByUsername(username).ifPresent(
			users -> users.updateRefreshToken(refreshToken)
		);
	}

	private void ignoreAuth(UserDetailsImpl userDetails, String userId) {
		UserRole userRole = userDetails.getUserRole();
		if((userRole == UserRole.CUSTOMER || userRole == UserRole.OWNER)
			&& !Objects.equals(userDetails.getUserId(), userId)){
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSION.getMessage());
		}
	}
}
