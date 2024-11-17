package com.nbcamp.orderservice.domain.order.dto;

import java.time.LocalDate;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.common.SortOption;

public record OrderSearchAdminRequest(
	OrderType orderType,
	LocalDate startDate,
	LocalDate endDate,
	OrderStatus orderStatus,
	SortOption sortOption

) {
	public OrderSearchAdminRequest(
		OrderType orderType,
		LocalDate startDate,
		LocalDate endDate,
		OrderStatus orderStatus,
		SortOption sortOption) {
		this.orderType = orderType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.orderStatus = orderStatus;
		this.sortOption = sortOption != null ? sortOption : SortOption.CREATED_AT_ASC;
	}
}
