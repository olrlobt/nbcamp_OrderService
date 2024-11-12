package com.nbcamp.orderservice.domain.store.dto;

import java.util.List;

public record StoreRequest(
	String name,
	List<String> category,
	String address,
	String callNumber
) {
}
