package com.nbcamp.orderservice.domain.order.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.order.entity.OrderProduct;

public record OrderProductResponse(
	UUID orderProductId,
	UUID productId,
	String productName,
	int quantity,
	int productTotalPrice
) {
	public OrderProductResponse(OrderProduct orderProduct){
		this(
			orderProduct.getId(),
			orderProduct.getProduct().getId(),
			orderProduct.getProduct().getName(),
			orderProduct.getQuantity(),
			orderProduct.getTotalPrice()
			);
	}
}
