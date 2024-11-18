package com.nbcamp.orderservice.domain.order.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.order.entity.Order;

public record OrderResponse(
	UUID orderId,
	UUID storeId,
	UUID userId,
	String storeName,
	OrderStatus status,
	OrderType type,
	String deliveryAddress,
	String content,
	int totalPrice
) {
	public OrderResponse(Order order) {
		this(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getStore().getName(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice()
		);
	}
}







