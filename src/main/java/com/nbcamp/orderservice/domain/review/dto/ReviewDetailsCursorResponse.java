package com.nbcamp.orderservice.domain.review.dto;

import java.util.UUID;

public record ReviewDetailsCursorResponse(
	UUID storeId,
	String storeName,
	UUID reviewId,
	String content,
	int grade
)  {
}
