package com.nbcamp.orderservice.domain.store.dto;

import java.util.UUID;

public record StoreDetailsResponse(
	UUID storeId,
	String ownerName,
	String name,
	String address,
	String callNumber
) {
}
