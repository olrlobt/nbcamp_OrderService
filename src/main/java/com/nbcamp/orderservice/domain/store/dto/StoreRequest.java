package com.nbcamp.orderservice.domain.store.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StoreRequest(
	@Size(max = 50)
	@NotNull(message = "매장명은 필수입니다")
	String name,
	@NotNull(message = "카테고리 정보는 필수입니다.")
	@NotEmpty(message = "카테고리 정보는 하나 이상 입력해야 합니다.")
	List<UUID> category,
	@Size(max = 200, message = "주소는 200자 이내로 작성해 주세요.")
	@NotNull(message = "주소는 필수 입력값입니다.")
	@Pattern(
		regexp = "([가-힣]+(특별시|광역시|도))\\s([가-힣]+구)(.*)?",
		message = "주소는 올바른 형식으로 입력해 주세요. 예: 서울특별시 강남구"
	)
	String address,
	@NotNull(message = "매장 전화번호는 필수입니다.")
	@Pattern(
		regexp = "(0\\d{1,2})-(\\d{3,4})-(\\d{4})",
		message = "전화번호는 올바른 형식으로 입력해 주세요. 예: 02-123-4567 또는 041-1234-5678"
	)
	String callNumber
) {
}
