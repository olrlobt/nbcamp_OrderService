package com.nbcamp.orderservice.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nbcamp.orderservice.domain.user.dto.LoginRequest;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final JwtService jwtService;
	private final UsersRepository usersRepository ;
	private final PasswordEncoder passwordEncoder;

	public String login(LoginRequest loginRequest) {
		User user = usersRepository.findByUsername(loginRequest.username())
			.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}
		return jwtService.createAccessToken(user.getUsername());
	}

	public void signup(SignupRequest signupRequest) {
		User user = User.create(signupRequest, passwordEncoder);
		usersRepository.save(user);
	}
}
