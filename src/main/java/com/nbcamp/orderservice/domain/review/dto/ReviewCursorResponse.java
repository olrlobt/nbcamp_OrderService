package com.nbcamp.orderservice.domain.review.dto;

import java.util.UUID;

public record ReviewCursorResponse(
	UUID reviewId,
	UUID userId,
	String userName,
	String content,
	int grade
) {
}
