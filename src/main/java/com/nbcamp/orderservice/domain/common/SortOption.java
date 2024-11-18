package com.nbcamp.orderservice.domain.common;

public enum SortOption {
	CREATED_AT_ASC,
	CREATED_AT_DESC,
	UPDATED_AT_ASC,
	UPDATED_AT_DESC,
	STAR_RATING_UP, // 리뷰 별점 높은순
	STAR_RATING_DOWN, // 리뷰 별점 낮은순
	STORE_STAR_RATING, // 스토어 별점 높은순
	MOST_ORDERS // 주문 많은순
}
