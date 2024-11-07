package com.nbcamp.orderservice.domain.product.dto;

import com.nbcamp.orderservice.domain.common.DisplayStatus;

public record CreateProductRequest(
	String name,
	String description,
	int price,
	DisplayStatus status
) {
}
