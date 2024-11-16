package com.nbcamp.orderservice.domain.store.dto;

import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.store.entity.Store;

public record StoreResponse(
	UUID storeId,
	UUID userId,
	String ownerName,
	String name,
	String address,
	List<UUID> category,
	String callNumber
) {
	public StoreResponse(Store store){
		this(
			store.getId(),
			store.getUser().getId(),
			store.getUser().getUsername(),
			store.getName(),
			store.getAddress(),
			store.getStoreCategory().stream().map(storeCategory -> storeCategory.getCategory().getId()).toList(),
			store.getCallNumber()
		);
	}

}
