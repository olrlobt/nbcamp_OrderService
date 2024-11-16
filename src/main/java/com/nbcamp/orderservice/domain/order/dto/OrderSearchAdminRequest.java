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
}
