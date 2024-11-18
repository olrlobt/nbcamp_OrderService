package com.nbcamp.orderservice.domain.order.dto;

import com.nbcamp.orderservice.domain.common.OrderStatus;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;

public record OrderUpdateRequest(
	@Nullable
	OrderStatus orderStatus,
	@Nullable
	@Pattern(
		regexp = "^([가-힣]+(특별시|광역시|도))\\s([가-힣]+([시군구]))\\s(.+)$",
		message = "주소는 '특별시/광역시/도 + 시/군/구' 형식으로 입력해 주세요."
	)
	String deliveryAddress,
	@Nullable
	String request

) {
}
