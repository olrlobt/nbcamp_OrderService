package com.nbcamp.orderservice.domain.order.dto;

import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;

public record OrderResponse(
	UUID orderId,
	OrderStatus status,
	UUID storeId,
	OrderType type,
	String address,
	String request,
	int price,
	List<OrderProductResponse> products
) {
	public record OrderProductResponse(
		UUID orderProductId,
		UUID productId,
		int quantity,
		int price) {
	}
}
