package com.nbcamp.orderservice.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewRequest(
	String comment,
	@Min(1)
	@Max(5)
	int grade
) {
}
