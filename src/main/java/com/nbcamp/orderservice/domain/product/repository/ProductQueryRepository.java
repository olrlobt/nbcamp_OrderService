package com.nbcamp.orderservice.domain.product.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.product.dto.ProductResponse;
import com.nbcamp.orderservice.domain.product.entity.QProduct;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QProduct product = QProduct.product;

	public Page<ProductResponse> findAllProductResponsesByStoreId(UUID storeId, Pageable pageable,
		SortOption sortOption) {
		List<ProductResponse> productResponses = jpaQueryFactory
			.select(
				Projections.constructor(
					ProductResponse.class,
					product.id,
					product.store.id,
					product.name,
					product.description,
					product.price,
					product.displayStatus
				))
			.from(product)
			.where(
				product.store.id.eq(storeId)
			)
			.orderBy(getOrderSpecifier(sortOption, product))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(product.count())  // count() 메서드를 사용하여 전체 개수 조회
			.from(product)
			.where(product.store.id.eq(storeId))
			.fetchOne();

		return new PageImpl<>(productResponses, pageable, total != null ? total : 0L);
	}

	public Page<ProductResponse> searchProducts(UUID storeId, Pageable pageable, String keyword,
		SortOption sortOption) {
		BooleanExpression keywordCondition = keyword != null && !keyword.isEmpty()
			? product.name.containsIgnoreCase(keyword).or(product.description.containsIgnoreCase(keyword))
			: null;

		List<ProductResponse> productResponses = jpaQueryFactory
			.select(
				Projections.constructor(
					ProductResponse.class,
					product.id,
					product.store.id,
					product.name,
					product.description,
					product.price,
					product.displayStatus
				))
			.from(product)
			.where(
				product.store.id.eq(storeId)
					.and(keywordCondition)
			)
			.orderBy(getOrderSpecifier(sortOption, product))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(product.count())
			.from(product)
			.where(product.store.id.eq(storeId).and(keywordCondition))
			.fetchOne();

		return new PageImpl<>(productResponses, pageable, total != null ? total : 0L);
	}

	private OrderSpecifier<?> getOrderSpecifier(SortOption sortOption, QProduct product) {
		return switch (sortOption) {
			case CREATED_AT_DESC -> product.createdAt.desc();
			case UPDATED_AT_ASC -> product.updatedAt.asc();
			case UPDATED_AT_DESC -> product.updatedAt.desc();
			default -> product.createdAt.asc();
		};
	}

}
