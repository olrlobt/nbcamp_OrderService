package com.nbcamp.orderservice.domain.product.dto;

import com.nbcamp.orderservice.domain.common.DisplayStatus;

public record ProductRequest(
	String name,
	String description,
	int price,
	DisplayStatus status
) {
}
