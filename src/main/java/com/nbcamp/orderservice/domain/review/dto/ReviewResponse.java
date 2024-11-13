package com.nbcamp.orderservice.domain.review.dto;

import java.util.UUID;

public record ReviewResponse(
	UUID reviewId,
	String content,
	int grade
) {
}
