package com.nbcamp.orderservice.domain.review.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.review.entity.Review;

public record ReviewResponse(
	UUID reviewId,
	String content,
	int grade
) {
	public ReviewResponse(Review review){
		this(
			review.getId(),
			review.getContent(),
			review.getGrade()
		);
	}
}
