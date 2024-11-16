package com.nbcamp.orderservice.domain.order.dto;

import com.nbcamp.orderservice.domain.common.OrderStatus;

import jakarta.annotation.Nullable;

public record OrderUpdateRequest(
	@Nullable
	OrderStatus orderStatus,
	@Nullable
	String deliveryAddress,
	@Nullable
	String request

) {
}
