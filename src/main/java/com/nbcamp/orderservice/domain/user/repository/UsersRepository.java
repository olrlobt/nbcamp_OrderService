package com.nbcamp.orderservice.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.user.entity.User;

public interface UsersRepository extends JpaRepository<User, UUID>{
	Optional<User> findByRefreshToken(String refreshToken);
	Optional<User> findByUsername(String username);
}
