package com.nbcamp.orderservice.domain.store.dto;

import java.util.List;
import java.util.UUID;

public record StoreResponse(
	UUID storeId,
	UUID userId,
	String ownerName,
	String name,
	String address,
	List<String> category,
	String callNumber
) {
}
