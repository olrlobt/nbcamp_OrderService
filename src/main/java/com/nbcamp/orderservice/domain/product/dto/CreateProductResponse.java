package com.nbcamp.orderservice.domain.product.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.DisplayStatus;

public record CreateProductResponse(
	UUID productId,
	UUID storeId,
	String name,
	String description,
	int price,
	DisplayStatus displayStatus
) {
}
