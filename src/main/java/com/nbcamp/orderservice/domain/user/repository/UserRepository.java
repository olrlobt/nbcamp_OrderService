package com.nbcamp.orderservice.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, UUID>, UserQueryRepository{
	Optional<User> findByRefreshToken(String refreshToken);
	Optional<User> findByUsernameAndDeletedAtIsNull(String username);

	Optional<User> findByUsername(String username);
}
