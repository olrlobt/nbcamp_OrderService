package com.nbcamp.orderservice.domain.store.dto;

import java.util.List;

public record StoreRequest(
	String name,
	List<String> category,
	String area,
	String address,
	String callNumber
) {
}
