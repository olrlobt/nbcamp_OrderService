package com.nbcamp.orderservice.domain.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.review.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {


}
