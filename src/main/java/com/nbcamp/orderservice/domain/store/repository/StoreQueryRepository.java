package com.nbcamp.orderservice.domain.store.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.order.entity.QOrder;
import com.nbcamp.orderservice.domain.store.dto.StoreCursorResponse;
import com.nbcamp.orderservice.domain.store.entity.QStore;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QStore qStore = QStore.store;
	QOrder qOrder = QOrder.order;

	public Slice<StoreCursorResponse> findAllByStorePageable(
		UUID storeId,
		UUID categoryId,
		String address,
		SortOption sortOption,
		Pageable pageable,
		boolean includeDelete
	) {

		List<StoreCursorResponse> storeList = jpaQueryFactory.query()
			.select(
				Projections.constructor(
					StoreCursorResponse.class,
					qStore.id,
					qStore.name,
					qStore.storeGrade
				)
			)
			.from(qStore)
			.where(
				cursorIdFiltering(storeId),
				categoryEquals(categoryId),
				addressContains(address), // 특정 주소 기준
				deleteFilter(includeDelete)
				)
			.orderBy(
				getOrderSpecifier(sortOption))
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = storeList.size() > pageable.getPageSize();
		if (hasNext) {
			storeList.remove(storeList.size() - 1);
		}

		return new SliceImpl<>(storeList, pageable, hasNext);
	}


	private BooleanExpression cursorIdFiltering(UUID storeId) {
		return storeId != null ? qStore.id.gt(storeId) : null;
	}

	private BooleanExpression categoryEquals(UUID category) {
		return category != null ? qStore.storeCategory.any().category.id.eq(category) : null;
	}

	private BooleanExpression addressContains(String address) {
		return address != null && !address.isEmpty() ? qStore.address.contains(address) : null;
	}

	private BooleanExpression deleteFilter(boolean includeDelete){
		return includeDelete ? null : qStore.deletedAt.isNull().and(qStore.deletedBy.isNull());
	}

	private OrderSpecifier<?> getOrderSpecifier(SortOption sortOption) {
		return switch (sortOption) {
			case CREATED_AT_DESC -> qStore.createdAt.desc();
			case UPDATED_AT_ASC -> qStore.updatedAt.asc();
			case UPDATED_AT_DESC -> qStore.updatedAt.desc();
			case STORE_STAR_RATING -> qStore.storeGrade.desc();
			case MOST_ORDERS -> orderByMostOrders();
			default -> qStore.createdAt.asc();
		};
	}

	private OrderSpecifier<Long> orderByMostOrders() {
		return new OrderSpecifier<>(
			com.querydsl.core.types.Order.DESC,
			JPAExpressions.select(qOrder.id.count())
				.from(qOrder)
				.where(qOrder.store.id.eq(qStore.id))
				.groupBy(qOrder.store.id)
				.orderBy(qOrder.id.count().desc())
		);
	}

}



