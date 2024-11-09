package com.nbcamp.orderservice.global.security;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
		try {
			UUID uuidConverted = UUID.fromString(uuid);
			User users = usersRepository.findById(uuidConverted)
				.orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + uuid));
			return new UserDetailsImpl(users);
		} catch (IllegalArgumentException e) {
			throw new UsernameNotFoundException("Invalid UUID format: " + uuid, e);
		}
	}
}
