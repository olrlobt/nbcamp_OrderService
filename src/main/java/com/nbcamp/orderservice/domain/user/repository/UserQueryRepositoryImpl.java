package com.nbcamp.orderservice.domain.user.repository;

import static com.nbcamp.orderservice.domain.user.entity.QUser.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.entity.QUser;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<UserResponse> findUserResponseByUserId(UUID userId) {
		return Optional.ofNullable(jpaQueryFactory.select(
				Projections.constructor(
					UserResponse.class,
					user.username,
					user.userRole
				)
			)
			.from(user)
			.where(user.deletedBy.isNull())
			.fetchOne());
	}

	@Override
	public Page<UserResponse> findAllUserResponse(SortOption sortOption, Pageable pageable) {
		int pageSize = pageable.getPageSize();
		long offset = pageable.getOffset();

		List<UserResponse> userResponses;
		userResponses = jpaQueryFactory
			.select(
				Projections.constructor(
					UserResponse.class,
					user.username,
					user.userRole
				)
			)
			.from(user)
			.where(user.deletedBy.isNull())
			.orderBy(getOrderSpecifier(sortOption, user))
			.offset(offset)
			.limit(pageSize)
			.fetch();

		Long total = jpaQueryFactory
			.select(user.count())
			.from(user)
			.where(user.deletedBy.isNull())
			.fetchOne();

		return new PageImpl<>(userResponses, pageable, total != null ? total : 0L);
	}

	private OrderSpecifier<?> getOrderSpecifier(SortOption sortOption, QUser user) {
		return switch (sortOption) {
			case CREATED_AT_DESC -> user.createdAt.desc();
			case UPDATED_AT_ASC -> user.updatedAt.asc();
			case UPDATED_AT_DESC -> user.updatedAt.desc();
			default -> user.createdAt.asc();
		};
	}
}
