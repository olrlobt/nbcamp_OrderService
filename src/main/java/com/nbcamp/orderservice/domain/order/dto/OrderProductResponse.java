package com.nbcamp.orderservice.domain.order.dto;

import java.util.UUID;

public record OrderProductResponse(
	UUID orderProductId,
	UUID productId,
	String productName,
	int quantity,
	int productTotalPrice
) {
}
