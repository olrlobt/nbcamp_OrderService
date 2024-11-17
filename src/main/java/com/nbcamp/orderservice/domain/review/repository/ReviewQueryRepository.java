package com.nbcamp.orderservice.domain.review.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.review.dto.ReviewCursorResponse;
import com.nbcamp.orderservice.domain.review.dto.ReviewDetailsCursorResponse;
import com.nbcamp.orderservice.domain.review.entity.QReview;
import com.nbcamp.orderservice.domain.review.entity.Review;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.user.entity.QUser;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QReview qReview = QReview.review;
	QUser qUser = QUser.user;

	public Slice<ReviewCursorResponse> getAllReviewInStore(Store store, Pageable pageable, SortOption sortOption, boolean includeDelete) {

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
				deleteFilter(includeDelete)
			)
			.orderBy(getOrderSpecifier(sortOption))
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = reviewList.size() > pageable.getPageSize();
		if (hasNext) {
			reviewList.remove(pageable.getPageSize());
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

	public Optional<Review> findByIdCustom(UUID reviewId){

		Review review = jpaQueryFactory.query()
			.select(qReview)
			.from(qReview)
			.where(
				qReview.id.eq(reviewId),
				qReview.deletedBy.isNull(),
				qReview.deletedAt.isNull()
			)
			.fetchOne();

		return Optional.ofNullable(review);

	}

	private BooleanExpression deleteFilter(boolean includeDelete){
		return includeDelete ? null : qReview.deletedAt.isNull().and(qReview.deletedBy.isNull());
	}


	private OrderSpecifier<?> getOrderSpecifier(SortOption sortOption) {
		return switch (sortOption) {
			case CREATED_AT_DESC -> qReview.createdAt.desc();
			case UPDATED_AT_ASC -> qReview.updatedAt.asc();
			case UPDATED_AT_DESC -> qReview.updatedAt.desc();
			default -> qReview.createdAt.asc();
		};
	}

}
