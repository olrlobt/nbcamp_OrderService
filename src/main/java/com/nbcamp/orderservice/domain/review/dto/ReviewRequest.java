package com.nbcamp.orderservice.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
	@NotNull(message = "리뷰 내용 작성은 필수입니다.")
	@Size(max = 500, message = "리뷰는 500자 이하로 작성해주세요")
	String content,
	@Min(1)
	@Max(5)
	@NotNull(message = "평점 등록은 필수입니다.")
	int grade
) {
}
