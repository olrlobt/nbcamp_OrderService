package com.nbcamp.orderservice.domain.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nbcamp.orderservice.domain.user.dto.AllUserResponse;
import com.nbcamp.orderservice.domain.user.dto.LoginRequest;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final JwtService jwtService;
	private final UserRepository usersRepository ;
	private final PasswordEncoder passwordEncoder;

	public String login(LoginRequest loginRequest) {
		User user = usersRepository.findByUsername(loginRequest.username())
			.orElseThrow(() -> new RuntimeException("Invalid username or password"));

		if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
			throw new RuntimeException("Invalid username or password");
		}
		return jwtService.createAccessToken(user.getUsername());
	}

	public void signup(SignupRequest signupRequest) {
		User user = User.create(signupRequest, passwordEncoder);
		usersRepository.save(user);
	}

	public void logout(String username) {
		jwtService.destroyRefreshToken(username);
	}

	public UserResponse getUserDetail(String userId) {
		//todo. 에러 상세화
		return usersRepository.findUserResponseByUserId(UUID.fromString(userId)).orElseThrow(IllegalArgumentException::new);
	}

	public AllUserResponse getAllUsers() {
		return usersRepository.findAllUserResponse();
	}
}
