package com.nbcamp.orderservice.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
	@Size(max = 500, message = "리뷰는 500자 이하로 작성해주세요")
	String content,
	@Min(1)
	@Max(5)
	int grade
) {
}
