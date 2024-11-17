package com.nbcamp.orderservice.domain.store.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.store.dto.StoreCursorResponse;
import com.nbcamp.orderservice.domain.store.entity.QStore;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QStore qStore = QStore.store;

	public Slice<StoreCursorResponse> findAllByStorePageable(
		UUID storeId,
		UUID categoryId,
		String address,
		SortOption sortOption,
		Pageable pageable,
		User user
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
				roleBasedDeleteFilter(user.getUserRole())
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

	private BooleanExpression roleBasedDeleteFilter(UserRole userRole) {
		if (userRole == UserRole.MASTER) {
			return null;
		}
		return qStore.deletedAt.isNull().and(qStore.deletedBy.isNull());
	}

	private OrderSpecifier<?> getOrderSpecifier(SortOption sortOption) {
		return switch (sortOption) {
			case CREATED_AT_DESC -> qStore.createdAt.desc();
			case UPDATED_AT_ASC -> qStore.updatedAt.asc();
			case UPDATED_AT_DESC -> qStore.updatedAt.desc();
			default -> qStore.createdAt.asc();
		};
	}
}



