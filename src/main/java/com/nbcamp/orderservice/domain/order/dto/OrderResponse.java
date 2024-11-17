package com.nbcamp.orderservice.domain.order.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.order.entity.Order;

public record OrderResponse(
	UUID orderId,
	UUID storeId,
	UUID userId,
	OrderStatus status,
	OrderType type,
	String deliveryAddress,
	String content,
	int totalPrice,
	String storeName
) {
	public OrderResponse(Order order) {
		this(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice(),
			order.getStore().getName()
		);
	}
}







