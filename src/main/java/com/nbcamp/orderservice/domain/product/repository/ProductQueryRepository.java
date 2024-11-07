package com.nbcamp.orderservice.domain.product.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.product.dto.ProductResponse;
import com.nbcamp.orderservice.domain.product.entity.QProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QProduct product = QProduct.product;

	public Page<ProductResponse> findAllProductResponsesByStoreId(UUID storeId, Pageable pageable) {
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
			.orderBy(product.createdAt.asc(), product.updatedAt.asc())
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

}
