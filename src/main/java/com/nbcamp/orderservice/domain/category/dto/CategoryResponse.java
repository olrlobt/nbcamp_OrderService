package com.nbcamp.orderservice.domain.category.dto;

import java.util.UUID;

public record CategoryResponse(
	UUID categoryId,
	String category
) {
}
