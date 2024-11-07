package com.nbcamp.orderservice.domain.common;

public enum DisplayStatus {
	EXPOSED,    // 노출됨 (상품이 사용자에게 보이는 상태)
	SOLD_OUT,   // 품절 (상품이 품절되어 더 이상 판매되지 않는 상태)
	DISCONTINUED // 단종 (상품이 더 이상 생산되지 않거나 판매되지 않는 상태)
}
