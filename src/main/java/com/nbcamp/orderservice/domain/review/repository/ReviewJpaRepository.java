package com.nbcamp.orderservice.domain.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.review.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {

	boolean existsByUserIdAndOrderId(UUID user_id, UUID order_id);

	boolean existsByIdAndUserId(UUID id, UUID user_id);
}
