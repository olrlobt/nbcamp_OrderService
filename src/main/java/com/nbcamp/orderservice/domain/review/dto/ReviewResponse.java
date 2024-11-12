package com.nbcamp.orderservice.domain.review.dto;

import java.util.UUID;

public record ReviewResponse(
	UUID uuid,
	String comment,
	int grade
) {
}
