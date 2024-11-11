package com.nbcamp.orderservice.domain.store.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.store.dto.StoreCursorResponse;
import com.nbcamp.orderservice.domain.store.entity.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QStore qStore = QStore.store;

	public Slice<StoreCursorResponse> findAllByStorePageable(
		String cursorId,
		String category,
		String address,
		Pageable pageable
	) {
		BooleanBuilder cursorFilter = new BooleanBuilder();

		if (cursorId != null) {
			UUID cursorUUID = UUID.fromString(cursorId);
			cursorFilter.and(qStore.id.gt(cursorUUID));
		}

		if (category != null) {
			cursorFilter.and(qStore.storeCategory.any().category.category.eq(category));
		}

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
				cursorFilter,
				qStore.address.contains(address),
				qStore.deletedAt.isNotNull().and(qStore.deletedBy.isNotNull())
				)
			.orderBy(
				qStore.storeGrade.desc(),
				qStore.createdAt.desc(),
				qStore.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = storeList.size() > pageable.getPageSize();
		if (hasNext) {
			storeList.remove(storeList.size() - 1);
		}

		UUID lastCursorId;
		if(storeList.isEmpty()){
			lastCursorId = null;
		} else {
			lastCursorId = storeList.get(storeList.size() - 1).storeId();
		}
		return new SliceImpl<>(storeList, pageable, hasNext);
	}

}



