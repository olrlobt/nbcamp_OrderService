package com.nbcamp.orderservice.global.security;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.repository.UserRepository;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			User user = usersRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
			return new UserDetailsImpl(user);
		} catch (IllegalArgumentException e) {
			throw new UsernameNotFoundException(ErrorCode.INVALID_UUID_FORMAT.getMessage());
		}
	}
}
