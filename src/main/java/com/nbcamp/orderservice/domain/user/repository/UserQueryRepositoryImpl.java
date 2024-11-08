package com.nbcamp.orderservice.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.entity.QUser;
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
					QUser.user
				)
			).from(QUser.user)
			.where(QUser.user.deletedBy.isNull())
			.fetchOne());
	}
}
