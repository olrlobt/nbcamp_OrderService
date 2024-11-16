package com.nbcamp.orderservice.domain.store.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.SortOption;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StoreCursorRequest(
	UUID storeId,
	UUID categoryId,
	@Size(max = 200, message = "주소는 200자 이내로 작성해 주세요.")
	@Pattern(
		regexp = "([가-힣]+(특별시|광역시|도))\\s([가-힣]+구)",
		message = "주소는 올바른 형식으로 입력해 주세요. 예: 서울특별시 강남구"
	)
	String address,
	SortOption sortOption
) {
	public StoreCursorRequest(UUID storeId, UUID categoryId, String address, SortOption sortOption) {
		this.storeId = storeId;
		this.categoryId = categoryId;
		this.address = address;
		this.sortOption = sortOption != null ? sortOption : SortOption.CREATED_AT_ASC; // 기본 값 설정
	}
}
