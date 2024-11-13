package com.nbcamp.orderservice.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.review.dto.ReviewCursorResponse;
import com.nbcamp.orderservice.domain.review.entity.QReview;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.user.entity.QUser;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QReview qReview = QReview.review;
	QUser qUser = QUser.user;

	public Slice<ReviewCursorResponse> getAllReviewInStore(Pageable pageable, Store store) {

		Pageable validatedPageable = validatePageSize(pageable);

		List<ReviewCursorResponse> reviewList = jpaQueryFactory.query()
			.select(
				Projections.constructor(
					ReviewCursorResponse.class,
					qReview.id,
					qUser.id,
					qUser.username,
					qReview.content,
					qReview.grade
				)
			)
			.from(qReview)
			.where(qReview.order.store.id.eq(store.getId()))
			.orderBy(getOrderSpecifier(pageable))
			.limit(validatedPageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = reviewList.size() > pageable.getPageSize();
		if (hasNext) {
			reviewList.remove(pageable.getPageSize());
		}

		return new SliceImpl<>(reviewList, pageable, hasNext);
	}

	private Pageable validatePageSize(Pageable pageable){
		int pageSize = pageable.getPageSize();
		if (pageSize == 10 || pageSize == 30 || pageSize == 50) {
			return pageable;
		} else {
			return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		}
	}

	private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable){
		return pageable
			.getSort()
			.stream()
			.map(order -> {
				String property = order.getProperty();
				if (property.equals("createdAt")) {
					if (order.isAscending()) {
						return qReview.createdAt.asc();
					} else {
						return qReview.createdAt.desc();
					}
				} else if (property.equals("updatedAt")) {
					if (order.isAscending()) {
						return qReview.updatedAt.asc();
					} else {
						return qReview.updatedAt.desc();
					}
				} else if (property.equals("grade")) {
					if (order.isAscending()) {
						return qReview.grade.asc();
					} else {
						return qReview.grade.desc();
					}
				}
				return qReview.createdAt.desc();
			}).toArray(OrderSpecifier[]::new);
	}

}
