package com.nbcamp.orderservice.domain.order.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.common.SortOption;

import jakarta.validation.constraints.Size;

public record OrderSearchCustomerRequest(
	@Size(max = 50, message = "매장명은 50자 이내로 작성해 주세요")
	String storeName,
	UUID categoryId,
	OrderType orderType,
	LocalDate startDate,
	LocalDate endDate,
	OrderStatus orderStatus,
	SortOption sortOption
) {
	public OrderSearchCustomerRequest(
		String storeName,
		UUID categoryId,
		OrderType orderType,
		LocalDate startDate,
		LocalDate endDate,
		OrderStatus orderStatus,
		SortOption sortOption) {
		this.storeName = storeName;
		this.categoryId = categoryId;
		this.orderType = orderType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.orderStatus = orderStatus;
		this.sortOption = sortOption != null ? sortOption : SortOption.CREATED_AT_ASC;
	}
}
