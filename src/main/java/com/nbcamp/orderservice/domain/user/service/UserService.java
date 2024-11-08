package com.nbcamp.orderservice.domain.user.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.user.dto.AllUserResponse;
import com.nbcamp.orderservice.domain.user.dto.LoginRequest;
import com.nbcamp.orderservice.domain.user.dto.LoginResponse;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.dto.UserUpdateRequest;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public LoginResponse login(LoginRequest loginRequest) {
		User user = userRepository.findByUsername(loginRequest.username())
			.orElseThrow(() -> new RuntimeException("Invalid username or password"));

		if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
			throw new RuntimeException("Invalid username or password");
		}

		return new LoginResponse(user.getUsername(),
			jwtService.createAccessToken(user.getUsername()));
	}

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
		//todo. 에러 상세화
		return userRepository.findUserResponseByUserId(UUID.fromString(userId)).orElseThrow(IllegalArgumentException::new);
	}

	@Transactional(readOnly = true)
	public AllUserResponse getAllUsers() {
		return userRepository.findAllUserResponse();
	}

	@Transactional
	public UserResponse updateUser(UserDetailsImpl userDetails, String userId, UserUpdateRequest request) {
		//todo. 에러 상세화
		ignoreAuth(userDetails, userId);

		User user = userRepository.findById(UUID.fromString(userId))
			.orElseThrow(IllegalArgumentException::new);
		user.update(request);
		return UserResponse.of(user);
	}

	@Transactional
	public void deleteUser(UserDetailsImpl userDetails, String userId) {
		ignoreAuth(userDetails, userId);
		User user = userRepository.findById(UUID.fromString(userId))
			.orElseThrow(IllegalArgumentException::new);
		user.delete();
	}

	private void ignoreAuth(UserDetailsImpl userDetails, String userId) {
		UserRole userRole = userDetails.getUserRole();
		if((userRole == UserRole.CUSTOMER || userRole == UserRole.OWNER)
			&& !Objects.equals(userDetails.getUserId(), userId)){
			throw new IllegalArgumentException();
		}
	}
}
