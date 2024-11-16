package com.nbcamp.orderservice.domain.order.dto;

import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.common.OrderType;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequest(
	@NotNull(message = "매장 정보입력은 필수 입니다")
	UUID storeId,
	@NotNull(message = "매장 주문 방식 입력은 필수입니다")
	OrderType type,
	@Size(max = 200, message = "주소는 200자 이내로 작성해 주세요.")
	@NotNull(message = "")
	String address,
	@Nullable
	@Size(max = 100, message = "요청사항은 100자 이내로 작성해 주세요")
	String request,
	@Min(value = 0, message = "음수는 불가능 합니다.")
	int price,
	@NotNull(message = "주문 상품 정보는 필수입니다")
	@Valid
	List<OrderProduct> products
) {
	public record OrderProduct(
		@NotNull(message = "상품 정보는 필수입니다.")
		UUID productId,
		@Min(value = 0, message = "음수는 불가능 합니다.")
		int quantity,
		@Min(value = 0, message = "음수는 불가능 합니다.")
		int price) {
	}
}
