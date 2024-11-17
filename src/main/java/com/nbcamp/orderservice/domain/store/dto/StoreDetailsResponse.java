package com.nbcamp.orderservice.domain.store.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.store.entity.Store;

public record StoreDetailsResponse(
	UUID storeId,
	String ownerName,
	String name,
	String address,
	String callNumber
) {
	public StoreDetailsResponse(Store store){
		this(
			store.getId(),
			store.getUser().getUsername(),
			store.getName(),
			store.getAddress(),
			store.getCallNumber()
		);
	}
}
