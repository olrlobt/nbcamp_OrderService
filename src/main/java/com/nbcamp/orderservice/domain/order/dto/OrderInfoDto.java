package com.nbcamp.orderservice.domain.order.dto;

import java.util.List;

public record OrderInfoDto(
	OrderResponse orderResponse,
	List<OrderProductResponse> orderProductResponse
) {
}
