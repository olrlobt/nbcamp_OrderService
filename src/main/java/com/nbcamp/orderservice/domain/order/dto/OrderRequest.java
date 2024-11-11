package com.nbcamp.orderservice.domain.order.dto;

import com.nbcamp.orderservice.domain.common.OrderType;

import java.util.List;
import java.util.UUID;

public record OrderRequest(
	UUID storeId,
	OrderType type,
	String address,
	String request,
	int price,
	List<OrderProduct> products
) {
	public record OrderProduct(
		UUID productId,
		int quantity,
		int price) {
	}
}
