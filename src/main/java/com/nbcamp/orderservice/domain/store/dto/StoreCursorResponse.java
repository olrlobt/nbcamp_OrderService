package com.nbcamp.orderservice.domain.store.dto;

import java.util.UUID;

public record StoreCursorResponse(
	UUID storeId,
	String storeName,
	double storeGrade
) {
}
