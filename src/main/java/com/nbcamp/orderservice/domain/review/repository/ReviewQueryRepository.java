package com.nbcamp.orderservice.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.review.dto.ReviewCursorResponse;
import com.nbcamp.orderservice.domain.review.dto.ReviewDetailsCursorResponse;
import com.nbcamp.orderservice.domain.review.entity.QReview;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.user.entity.QUser;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QReview qReview = QReview.review;
	QUser qUser = QUser.user;

	public Slice<ReviewCursorResponse> getAllReviewInStore(Store store, Pageable pageable) {

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
			.where(
				qReview.order.store.id.eq(store.getId()),
				qReview.deletedAt.isNull(),
				qReview.deletedBy.isNull()
			)
			.orderBy(getOrderSpecifier(validatedPageable))
			.limit(validatedPageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = reviewList.size() > pageable.getPageSize();
		if (hasNext) {
			reviewList.remove(validatedPageable.getPageSize());
		}

		return new SliceImpl<>(reviewList, pageable, hasNext);
	}

	public Slice<ReviewDetailsCursorResponse> getAllReviewInUser(User user, Pageable pageable){

		List<ReviewDetailsCursorResponse> reviewList = jpaQueryFactory.query()
			.select(
				Projections.constructor(
					ReviewDetailsCursorResponse.class,
					qReview.order.store.id,
					qReview.order.store.name,
					qReview.id,
					qReview.content,
					qReview.grade
				)
			)
			.from(qReview)
			.where(
				qReview.user.id.eq(user.getId()),
				qReview.deletedAt.isNull(),
				qReview.deletedBy.isNull()
			)
			.orderBy(
				qReview.createdAt.desc(),
				qReview.updatedAt.desc()
			)
			.limit(pageable.getPageSize() + 1)
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
				PathBuilder pathBuilder = new PathBuilder<>(qReview.getType(), qReview.getMetadata());
					return new OrderSpecifier(
						order.isAscending() ? Order.ASC : Order.DESC,
						pathBuilder.get(order.getProperty()));
			}).toArray(OrderSpecifier[]::new);
	}

}
