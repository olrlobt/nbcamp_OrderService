package com.nbcamp.orderservice.domain.order.dto;

import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.common.OrderType;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
	@NotNull
	UUID storeId,
	@NotNull
	OrderType type,
	@NotNull
	String address,
	@Nullable
	String request,
	@Min(0)
	int price,
	@NotNull
	@Valid
	List<OrderProduct> products
) {
	public record OrderProduct(
		@NotNull
		UUID productId,
		@Min(0)
		int quantity,
		@Min(0)
		int price) {
	}
}
