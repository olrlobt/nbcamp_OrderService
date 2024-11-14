package com.nbcamp.orderservice.domain.order.dto;

import java.util.List;

public record OrderInfoResponse(
	OrderResponse orderResponse,
	List<OrderProductResponse> orderProductResponse
) {
}
