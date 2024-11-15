package com.nbcamp.orderservice.domain.order.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;

public record OrderResponse(
	UUID orderId,
	UUID storeId,
	UUID userId,
	OrderStatus status,
	OrderType type,
	String deliveryAddress,
	String content,
	int totalPrice
) {
}
