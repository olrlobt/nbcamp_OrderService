package com.nbcamp.orderservice.domain.store.dto;

import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.category.entity.Category;

public record StoreResponse(
	UUID storeId,
	UUID userId,
	String ownerName,
	String name,
	String area,
	String address,
	List<Category> category,
	String callNumber
) {
}
